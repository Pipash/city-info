package com.spring.camel.service;

import com.spring.camel.generator.CSVFileGenerator;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final CSVFileGenerator csvFileGenerator;
    @Value("${input.file.path}")
    private String filePath;
    @Value("${output.file.path}")
    private String outputFilePath;

    @Value("${open-weather.api-key}")
    private String apiKey;

    public String getWeatherByCity() throws IOException, InterruptedException {
        // initializing list for city weather
        List<String[]> cityWeather = new ArrayList<>();
        // setting headers
        cityWeather.add(new String[] {"Country", "City", "Date", "Weather-Details"});
        // reading csv files
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        //skip header line
        br.readLine();

        // read till the last line
        while ((line = br.readLine()) != null) {
            String[] city = line.split(",");
            // http request to get api data
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q="+ URLEncoder.encode(city[1], StandardCharsets.UTF_8)+"&appid="+apiKey))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //making jsong object from response body
            JSONObject json = new JSONObject(response.body());
            // check whether json contains valid data
            if (json.get("cod").equals(200)) {
                // getting information of weather
                JSONArray jsonArray = new JSONArray(json.get("weather").toString());
                JSONObject weather = new JSONObject(jsonArray.get(0).toString());
                // setting city weather list by csv file data and weather api data
                cityWeather.add(new String[] {city[0], city[1], city[2], weather.get("description").toString()});
            }

        }
        // generate csv file with the prepared data
        Boolean csvFile = csvFileGenerator.generateCSV(cityWeather, outputFilePath + "city-weather.csv");
        if (csvFile) {
            return "File has been generated successfully!";
        }

        return "No File has been Generated";
    }
}
