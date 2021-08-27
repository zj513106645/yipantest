package com.test.weather.service.impl;

import com.test.weather.WeatherMain8060;
import com.test.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest(classes={WeatherMain8060.class})
class WeatherServiceImplTest {

    @Autowired
    WeatherService weatherService;

    @Test
    void getTemperature() {
        //正常测试
        Optional<Integer> temp1 = weatherService.getTemperature("江苏", "苏州", "苏州");
        System.out.println("temp1:"+temp1.toString());
        //错误-998 错误的省代码
        Optional<Integer> temp2 = weatherService.getTemperature("我将省", "苏州市", "苏州");
        System.out.println("temp2:"+temp2.toString());
        //错误-997 错误的市代码
        Optional<Integer> temp3 = weatherService.getTemperature("江苏", "吴县市", "苏州");
        System.out.println("temp3:"+temp3.toString());
        //错误-996 错误的区代码
        Optional<Integer> temp4 = weatherService.getTemperature("江苏", "苏州", "无锡");
        System.out.println("temp4:"+temp4.toString());
    }


}