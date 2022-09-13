package com.read.data.openLibrary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/trending")
public class ReadTrendingDataController {

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/daily")
    public boolean daily() {
        log.info("Started reading and writing daily trending book details");
        URL url;
        try {
            url = new URL("https://openlibrary.org/trending/daily.json");
            log.info("URL: {}", url);
            String jsonResponse = IOUtils.toString(url, Charset.defaultCharset());
            writeJsonResponseIntoFile(jsonResponse);
            String csvResponse = buildCsvResponse(jsonResponse);
            writeCsvResponseIntoFile(csvResponse);
            log.info("completed reading and writing daily trending book details");
            return true;
        } catch (IOException e) {
            log.error("failed to read and write daily trending book details");
            log.error(e);
            return false;
        }
    }

    private String buildCsvResponse(String response) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode works = jsonNode.get("works");
        StringJoiner newLineStrJoiner = new StringJoiner("\r\n");
        if (JsonNodeType.ARRAY.equals(works.getNodeType())) {
            int bookNum = -1;
            JsonNode bookJsonNode;
            while (null != (bookJsonNode = works.get(++bookNum))) {
                newLineStrJoiner.add(getBookDetails(bookJsonNode));
            }
            log.info("all book details are {}", newLineStrJoiner);
        } else {
            log.warn("books details are empty");
        }
        return newLineStrJoiner.toString().replaceAll("\"", "");
    }

    private String getBookDetails(JsonNode bookJsonNode) {
        StringJoiner commaStrJoiner = new StringJoiner(",");
        String title = String.valueOf(bookJsonNode.get("title"));
        commaStrJoiner.add(title.replaceAll(",",""));
        String authors = getAuthors(bookJsonNode, title);
        commaStrJoiner.add(authors);
        String availabilityStatus = getAvailabilityStatus(bookJsonNode);
        commaStrJoiner.add(availabilityStatus);
        log.info("book details are title: {}, authors: {} and availability status: {}", title, authors, availabilityStatus);
        return commaStrJoiner.toString();
    }

    private String getAvailabilityStatus(JsonNode bookJsonNode) {
        JsonNode availabilityJsonNode = bookJsonNode.get("availability");
        String availabilityStatus = "UNKNOWN";
        if (null != availabilityJsonNode) {
            availabilityStatus = String.valueOf(availabilityJsonNode.get("status"));
        }
        return availabilityStatus.replaceAll(",","");
    }

    private String getAuthors(JsonNode bookJsonNode, String title) {
        HashSet<String> authors = new HashSet<>();
        JsonNode authorsJsonNode = bookJsonNode.get("author_name");
        if (null != authorsJsonNode) {
            if (JsonNodeType.ARRAY.equals(authorsJsonNode.getNodeType())) {
                int authorNum = -1;
                JsonNode author;
                while (null != (author = authorsJsonNode.get(++authorNum))) {
                    authors.add(String.valueOf(author).replaceAll(",",""));
                }
                log.info("authors size: {} for the title: {}", authors.size(), title);
            }
        } else {
            log.warn("authors are empty for title: {}", title);
            return "UNKNOWN";
        }
        return String.join(" && ", authors);
    }

    private void writeCsvResponseIntoFile(String csvResponse) {
        log.info("Started writing all book details into csv file");
        //todo: get filePath from props
        String filePath = "C:\\Users\\RavindraReddyAmbati\\Documents\\open library csv files\\";
        String fileName = filePath + "daily_trending_books_" + System.currentTimeMillis() + "_.csv";
        FileWriter csvFile = null;
        try {
            csvFile = new FileWriter(fileName);
            csvFile.write(csvResponse);
            csvFile.close();
        } catch (IOException e) {
            log.error("failed write daily trending book details into json file");
            log.error(e);
        }
        log.info("Completed writing all book details into csv file");
    }

    private void writeJsonResponseIntoFile(String response) {
        log.info("Started writing all book details into json file");
        //todo: get filePath from props
        String filePath = "C:\\Users\\RavindraReddyAmbati\\Documents\\open library json files\\";
        String fileName = filePath + "daily_trending_books_" + System.currentTimeMillis() + "_.json";
        FileWriter jsonFile = null;
        try {
            jsonFile = new FileWriter(fileName);
            jsonFile.write(response);
            jsonFile.close();
        } catch (IOException e) {
            log.error("failed write daily trending book details into json file");
            log.error(e);
        }
        log.info("Completed writing all book details into json file");
    }
}
