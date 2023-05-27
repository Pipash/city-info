package com.spring.camel.controller;

import com.spring.camel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class CityHotelController {
    private final FluentProducerTemplate producerTemplate;
    private final HotelService hotelService;
    @GetMapping("/city")
    public String getCity() throws IOException, InterruptedException {
        String response = hotelService.getAllHotels();
        return producerTemplate.withBody(response).to("direct:cityRoute").request(String.class);
    }
}
