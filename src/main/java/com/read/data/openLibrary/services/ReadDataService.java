package com.read.data.openLibrary.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.StringJoiner;

@Log4j2
@Service
public class ReadDataService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ReadBookDataService readBookDataService;

    @Autowired
    private WriteDataService writeDataService;

    public String readTrendingBooksDetails(String trendingType, URL url) throws IOException {
        log.info("Started reading and writing trending book details for url: {}",url);
        JsonNode jsonResponse = objectMapper.readTree(url);
        writeDataService.writeDataIntoFile(trendingType, jsonResponse.toString(), ".json");
        String csvResponse = buildCsvResponse(jsonResponse);
        String fileFullPath = writeDataService.writeDataIntoFile(trendingType, csvResponse, ".csv");
        log.info("completed reading and writing daily trending book details fileFullPath: {}",fileFullPath);
        return fileFullPath;
    }

    private String buildCsvResponse(JsonNode responseJsonNode) throws JsonProcessingException {
        JsonNode works = responseJsonNode.get("works");
        StringJoiner newLineStrJoiner = new StringJoiner("\r\n");
        newLineStrJoiner.add(getHeaders());
        if (JsonNodeType.ARRAY.equals(works.getNodeType())) {
            int bookNum = -1;
            JsonNode bookJsonNode;
            while (null != (bookJsonNode = works.get(++bookNum))) {
                newLineStrJoiner.add(readBookDataService.getBookDetails(bookJsonNode));
            }
            log.debug("all book details are {}", newLineStrJoiner);
        } else {
            log.warn("books details are empty");
        }
        return newLineStrJoiner.toString().replaceAll("\"", "");
    }

    private String getHeaders() {
        StringJoiner commaStrJoiner = new StringJoiner(",");
        commaStrJoiner.add("key");
        commaStrJoiner.add("title");
        commaStrJoiner.add("author");
        commaStrJoiner.add("availability status");
        commaStrJoiner.add("language");
        return commaStrJoiner.toString();
    }
}
