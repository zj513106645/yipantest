package com.test.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@EnableRetry
public class WeatherMain8060 {

    public static void main(String[] args) {
        SpringApplication.run(WeatherMain8060.class,args);
    }
}
