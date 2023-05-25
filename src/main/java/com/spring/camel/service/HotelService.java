package com.spring.camel.service;

import com.spring.camel.DTO.CityHotel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HotelService {

    public void getAllHotels(List<CityHotel> cityHotels) {
        log.info("get-hotels-called");
        cityHotels.forEach(cityHotel -> {
            cityHotel.setHotel("new hotel");
        });
    }
}
