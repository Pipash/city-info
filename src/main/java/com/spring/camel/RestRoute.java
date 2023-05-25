package com.spring.camel;


import com.spring.camel.DTO.CityHotel;
import com.spring.camel.service.HotelService;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
//@AllArgsConstructor
public class RestRoute extends RouteBuilder {
    //@Autowired
    private final HotelService hotelService;

    public RestRoute(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Value("${input.file.path}")
    private String filePath;

    @Override
    public void configure() throws Exception {
        // Configure the CSV data format
        CsvDataFormat csvDataFormat = new CsvDataFormat();
        csvDataFormat.setDelimiter(","); // Set the delimiter used in the CSV file
        // REST endpoint to get all hotels info
        rest("/get-hotels")
                .get()
                .to("direct:readHotelCityData");

        from("direct:readHotelCityData")
                .setBody().constant(filePath)
                .convertBodyTo(File.class)
                .unmarshal(csvDataFormat)
                .split(body())
                //.unmarshal().bindy(BindyType.Csv, CityHotel.class)
                .aggregate(constant(true), (oldExchange, newExchange) -> {
                    if (oldExchange == null) {
                        // First exchange, create a new ArrayList
                        List<CityHotel> list = new ArrayList<>();
                        list.add(newExchange.getIn().getBody(CityHotel.class));
                        newExchange.getIn().setBody(list);
                        return newExchange;
                    } else {
                        // Subsequent exchanges, add to the existing ArrayList
                        List<CityHotel> list = oldExchange.getIn().getBody(List.class);
                        list.add(newExchange.getIn().getBody(CityHotel.class));
                        return oldExchange;
                    }
                })
                .completionTimeout(2000)
                .bean(hotelService, "getAllHotels")
                .marshal(csvDataFormat)
                .convertBodyTo(String.class)
                .to("file:{{output.file.path}}");

        from("direct:cityRoute")
                .log("check route");

    }
}
