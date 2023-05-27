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

    public String getAllHotels() throws IOException, InterruptedException {
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

        while ((line = br.readLine()) != null) {
            String[] city = line.split(",");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://hotels4.p.rapidapi.com/locations/v3/search?q="+ URLEncoder.encode(city[1], StandardCharsets.UTF_8)))
                    .header("X-RapidAPI-Key", rapidKey)
                    .header("X-RapidAPI-Host", rapidHost)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body().replace("@type", "type1"));

            // check whether json return ok or not
            if (json.get("rc").equals("OK")) {
                JSONArray jsonArray = new JSONArray(json.get("sr").toString());
                int count =0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = new JSONObject(jsonArray.get(i).toString());
                    if (jsonObj.get("type").equals("HOTEL") && count < 3) {
                        JSONObject regionNames = new JSONObject(jsonObj.get("regionNames").toString());
                        cityHotels.add(new String[] {city[0], city[1], city[2], regionNames.get("shortName").toString()});
                        count++;
                    }
                }
            }
        }
        Boolean csvFile = csvFileGenerator.generateCSV(cityHotels, outputFilePath + "city-hotels.csv");
        if (csvFile) {
            return "File generated successfully!";
        }

        return "No File Generated";
    }
}
