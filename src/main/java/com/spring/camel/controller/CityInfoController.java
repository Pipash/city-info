package com.spring.camel.controller;

import com.spring.camel.service.HotelService;
import com.spring.camel.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class CityInfoController {
    private final FluentProducerTemplate producerTemplate;
    private final HotelService hotelService;
    private final WeatherService weatherService;

    @GetMapping("/city-hotels")
    public String getHotel() throws IOException, InterruptedException {
        // fetch csv data and generating new csv file with api data
        String response = hotelService.getAllHotels();
        return producerTemplate.withBody(response).to("direct:cityHotelsRoute").request(String.class);
    }

    @GetMapping("/city-weather")
    public String getWeather() throws IOException, InterruptedException {
        // fetch csv data and generating new csv file with api data
        String response = weatherService.getWeatherByCity();
        return producerTemplate.withBody(response).to("direct:cityWeatherRoute").request(String.class);
    }
}
