package com.test.weather.service.impl;

import com.test.weather.WeatherMain8060;
import com.test.weather.service.WeatherService;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * 多线程测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={WeatherMain8060.class})
class WeatherServiceImplMultiTest {

    @Autowired
    WeatherService weatherService;

    @Test
    void getTemperatureMultiThread() throws Throwable{
        TestRunnable[] trs = new TestRunnable[100];
        for(int i=0;i<100;i++){
            trs[i]=new ThreadTemp();
        }
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

        mttr.runTestRunnables();
    }

    private class ThreadTemp extends TestRunnable {
        @Override
        public void runTest() throws Throwable{
            getTempMultiThread();
        }
    }

    void getTempMultiThread() throws Exception {
        Optional<Integer> temp = weatherService.getTemperature("江苏省", "苏州市", "吴中区");
    }
}