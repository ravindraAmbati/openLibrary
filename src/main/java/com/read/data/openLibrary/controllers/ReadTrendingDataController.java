package com.read.data.openLibrary.controllers;

import com.read.data.openLibrary.services.ReadDataService;
import com.read.data.openLibrary.utils.UrlsBuilderUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

//todo: enable swagger-ui

@Log4j2
@RestController
@RequestMapping("/trending")
public class ReadTrendingDataController {

    @Autowired
    private UrlsBuilderUtil urlsBuilderUtil;
    @Autowired
    private ReadDataService readDataService;

    @GetMapping("/{type}")
    public @ResponseBody String readTrendingBooksDetails(@PathVariable("type") String trendingType) {
        try {
            String fileFullPath = readDataService.readTrendingBooksDetails(trendingType, urlsBuilderUtil.getUrl(trendingType));
            return String.format("Data loaded into %s", fileFullPath);
        } catch (IOException e) {
            log.error("failed to read daily trending book details");
            log.error(e);
            return "failed to read daily trending book details";
        }
    }

}
