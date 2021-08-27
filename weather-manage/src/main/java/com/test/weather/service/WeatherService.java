package com.test.weather.service;

import java.util.Optional;


public interface WeatherService {

     Optional<Integer> getTemperature(String province,String city,String country);
}
