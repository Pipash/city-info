package com.spring.camel.service;

import com.spring.camel.generator.CSVFileGenerator;
import com.spring.camel.generator.FetchCSVFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RequiredArgsConstructor
public class HotelService {
    private final CSVFileGenerator csvFileGenerator;
    private final FetchCSVFiles fetchCSVFiles;
    @Value("${input.file.path}")
    private String filePath;
    @Value("${output.file.path}")
    private String outputFilePath;

    @Value("${RapidAPI-Key}")
    private String rapidKey;

    @Value("${RapidAPI-Host}")
    private String rapidHost;

    /**
     * Get all hotels by city name and generate csv
     *
     * @return String
     * @throws IOException
     * @throws InterruptedException
     */
    public String getAllHotels() throws IOException, InterruptedException {
        // make a list for city hotels
        List<String[]> cityHotels = new ArrayList<>();
        // setting headers
        cityHotels.add(new String[] {"Country", "City", "Date", "Hotel"});

        //download files from ftp to local directory
        //fetchCSVFiles.downloadFiles();

        // reading csv files
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        //skip header line
        br.readLine();

        // reading file until last line
        while ((line = br.readLine()) != null) {
            String[] city = line.split(",");
            // http request to get api data
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://hotels4.p.rapidapi.com/locations/v3/search?q="+ URLEncoder.encode(city[1], StandardCharsets.UTF_8)))
                    .header("X-RapidAPI-Key", rapidKey)
                    .header("X-RapidAPI-Host", rapidHost)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //making jsong object from response body
            JSONObject json = new JSONObject(response.body().replace("@type", "type1"));

            // check whether json contains valid data
            if (json.get("rc").equals("OK")) {
                // making json array from json list object
                JSONArray jsonArray = new JSONArray(json.get("sr").toString());
                int count =0;
                // looping through json array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = new JSONObject(jsonArray.get(i).toString());
                    // if type of the data is hotel then count and get three hotels of the city
                    if (jsonObj.get("type").equals("HOTEL") && count < 3) {
                        JSONObject regionNames = new JSONObject(jsonObj.get("regionNames").toString());
                        // adding data to city hotel list with hotel short name from json data
                        cityHotels.add(new String[] {city[0], city[1], city[2], regionNames.get("shortName").toString()});
                        count++;
                    }
                }
            }
        }
        // generate csv file with the prepared data
        Boolean csvFile = csvFileGenerator.generateCSV(cityHotels, outputFilePath + "city-hotels.csv");
        if (csvFile) {
            return "File generated successfully!";
        }

        return "No File Generated";
    }
}
