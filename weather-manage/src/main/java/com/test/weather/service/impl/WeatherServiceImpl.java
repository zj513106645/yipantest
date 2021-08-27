package com.test.weather.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.test.weather.service.WeatherService;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {

    //限流，令牌桶设置2个令牌
    private static RateLimiter limiter = RateLimiter.create(2);
    static int i = 0;

    /**
     * 获取气温
     *
     * @param province 省中文名称
     * @param city     市中文名称
     * @param country  区中文名称
     * @return 气温
     */
    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 4, backoff = @Backoff(delay = 2000L, multiplier = 1))
    public Optional<Integer> getTemperature(String province, String city, String country) {
//        System.out.println("重试次数：" + i);
        System.out.println("线程等待时间：" + limiter.acquire(1) + "________");
        int x = 3 / i++;
        return getTemp(province, city, country);
    }

    /**
     * 获取气温
     *
     * @param province 省代码
     * @param city     市代码
     * @param country  区代码
     * @return 气温
     */
    private Optional<Integer> getTemp(String province, String city, String country) {
        String provinceCode;
        String cityCode;
        String countryCode;
        //远程调用接口 如错误则返回错误码
        String allProvinces = HttpUtil.get("http://www.weather.com.cn/data/city3jdata/china.html", CharsetUtil.CHARSET_UTF_8);
        if (!JSONUtil.isJson(allProvinces)) {
            return Optional.of(-999);
        } else {
            provinceCode = getCodeByValue(JSONUtil.parseObj(allProvinces), province);
            if (StringUtils.isBlank(provinceCode)) {
                return Optional.of(-999);
            }
        }
        String citiesOfProvince = HttpUtil.get("http://www.weather.com.cn/data/city3jdata/provshi/" + provinceCode + ".html", CharsetUtil.CHARSET_UTF_8);
        if (!JSONUtil.isJson(citiesOfProvince)) {
            return Optional.of(-998);
        } else {
            cityCode = getCodeByValue(JSONUtil.parseObj(citiesOfProvince), city);
            if (StringUtils.isBlank(cityCode)) {
                return Optional.of(-998);
            }
        }
        String countriesOfCity = HttpUtil.get("http://www.weather.com.cn/data/city3jdata/station/" + provinceCode + cityCode + ".html", CharsetUtil.CHARSET_UTF_8);
        if (!JSONUtil.isJson(countriesOfCity)) {
            return Optional.of(-997);
        } else {
            countryCode = getCodeByValue(JSONUtil.parseObj(countriesOfCity), country);
            if (StringUtils.isBlank(countryCode)) {
                return Optional.of(-997);
            }
        }
        String countryWeather = HttpUtil.get("http://www.weather.com.cn/data/sk/" + provinceCode + cityCode + countryCode + ".html", CharsetUtil.CHARSET_UTF_8);
        if (!JSONUtil.isJson(countryWeather)) {
            return Optional.of(-996);
        } else {
            //获得天气对象
            JSONObject jsonObject = JSONUtil.parseObj(countryWeather);
            Object weatherinfo = jsonObject.get("weatherinfo");
            if (!JSONUtil.isNull(weatherinfo)) {
                //找到温度
                JSONObject weather = JSONUtil.parseObj(weatherinfo);
                Integer temp = Math.round(Float.parseFloat((String) weather.get("temp")));
                System.out.println("线程：" + Thread.currentThread().getName() + ",temp:" + temp.toString());
                return Optional.of(temp);
            } else {
                return Optional.of(-995);
            }
        }
    }

    /**
     * 重试机制
     *
     * @param e
     * @return
     */
    @Recover
    public Optional<Integer> recover(Exception e) {
        System.out.println("重试失败");
        e.printStackTrace();
        return Optional.of(-995);
    }

    //    private String getCodeByValue(JSONObject jo, String inputValue) {
//        JSONObject provinceJsonObject = JSONUtil.parseObj(jo);
//        Iterator<String> iterator = provinceJsonObject.keySet().iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            String value = provinceJsonObject.getStr(key);
//            if (inputValue.contains(value)) {
//                return key;
//            }
//        }
//        return null;
//    }
    private String getCodeByValue(JSONObject jo, String inputValue) {
        JSONObject provinceJsonObject = JSONUtil.parseObj(jo);
        Optional<String> key = provinceJsonObject.keySet().
                stream().filter(e -> inputValue.contains((String) provinceJsonObject.get(e))).findFirst();
        if (key.isPresent()) {
            return key.get();
        } else {
            return null;
        }
    }
}
