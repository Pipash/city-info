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
    //https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

    private final CSVFileGenerator csvFileGenerator;
    @Value("${input.file.path}")
    private String filePath;
    @Value("${output.file.path}")
    private String outputFilePath;

    @Value("${open-weather.api-key}")
    private String apiKey;

    public String getWeatherByCity() throws IOException, InterruptedException {
        List<String[]> cityWeather = new ArrayList<>();
        // setting headers
        cityWeather.add(new String[] {"Country", "City", "Date", "Weather-Details"});
        // reading csv files
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        //skip header line
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] city = line.split(",");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q="+ URLEncoder.encode(city[1], StandardCharsets.UTF_8)+"&appid="+apiKey))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //System.out.println(response.body());
            JSONObject json = new JSONObject(response.body());
            //JSONObject json = new JSONObject("{\"cod\":\"404\",\"message\":\"city not found\"}");
            //JSONObject json = new JSONObject("{\"coord\":{\"lon\":10.7461,\"lat\":59.9127},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\",\"main\":{\"temp\":290.52,\"feels_like\":289.25,\"temp_min\":289.33,\"temp_max\":292.77,\"pressure\":1021,\"humidity\":36,\"sea_level\":1021,\"grnd_level\":1017},\"visibility\":10000,\"wind\":{\"speed\":4.38,\"deg\":195,\"gust\":5.45},\"clouds\":{\"all\":52},\"dt\":1685182237,\"sys\":{\"type\":2,\"id\":237284,\"country\":\"NO\",\"sunrise\":1685153759,\"sunset\":1685218356},\"timezone\":7200,\"id\":3143244,\"name\":\"Oslo\",\"cod\":200}");
            //System.out.println(json.get("cod").equals("200"));
            if (json.get("cod").equals(200)) {
                JSONArray jsonArray = new JSONArray(json.get("weather").toString());
                JSONObject weather = new JSONObject(jsonArray.get(0).toString());
                //System.out.println(weather.get("description"));
                cityWeather.add(new String[] {city[0], city[1], city[2], weather.get("description").toString()});
            }

        }
        Boolean csvFile = csvFileGenerator.generateCSV(cityWeather, outputFilePath + "city-weather.csv");
        if (csvFile) {
            return "File has been generated successfully!";
        }

        return "No File has been Generated";
    }
}
