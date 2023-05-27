package com.spring.camel.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CSVFileGenerator {
    // generate csv file
    public Boolean generateCSV(List<String[]> data, String outputFilePath) throws IOException {
        File csvOutputFile = new File(outputFilePath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        return  csvOutputFile.exists();
    }

    // convert string data to csv type delimiter data
    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    // escaping special characters
    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
