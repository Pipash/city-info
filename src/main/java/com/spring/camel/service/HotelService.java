package com.spring.camel.service;

import com.spring.camel.DTO.CityHotel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    public void getAllHotels(List<CityHotel> cityHotels) {
        cityHotels.forEach(cityHotel -> {
            cityHotel.setHotel("new hotel");
        });
    }
}
