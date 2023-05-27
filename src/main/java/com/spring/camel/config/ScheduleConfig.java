package com.spring.camel.config;

import com.spring.camel.service.HotelService;
import com.spring.camel.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {
    private final HotelService hotelService;
    private final WeatherService weatherService;

    @Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    public void getHotelSchedule() throws IOException, InterruptedException {
        String response = hotelService.getAllHotels();
        System.out.println(response);

        long milliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultDate = new Date(milliseconds);
        System.out.println(
                "Fixed delay task - " + sdf.format(resultDate));
    }

    @Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    public void getWeatherSchedule() throws IOException, InterruptedException {
        String response = weatherService.getWeatherByCity();
        System.out.println(response);

        long milliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultDate = new Date(milliseconds);
        System.out.println(
                "Fixed delay task - " + sdf.format(resultDate));
    }

}
