package com.spring.camel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityHotel {
    private String country;
    private String city;
    private String date;
    private String hotel;
}
