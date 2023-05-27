package com.spring.camel;

import com.spring.camel.service.HotelService;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:cityHotelsRoute")
                .log("get information of city hotel");

        from("direct:cityWeatherRoute")
                .log("get information of city weather");

    }
}
