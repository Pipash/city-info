package com.spring.camel.controller;

import com.spring.camel.DTO.CityHotel;
import lombok.AllArgsConstructor;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class CityHotelController {
    private final FluentProducerTemplate producerTemplate;

    @GetMapping("/city")
    public String getCity() {
        List<CityHotel> cityHotels = new ArrayList<>();
        
        return producerTemplate.withBody("body goes here").to("direct:cityRoute").request(String.class);
    }
}
