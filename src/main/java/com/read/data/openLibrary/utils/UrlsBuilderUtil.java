package com.read.data.openLibrary.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

@Log4j2
@Service
public class UrlsBuilderUtil {

    @Value("${openLibraryUrl}")
    private String hostname;
    @Value("${trendingNowEndpoint}")
    private String trendingNowEndpoint;
    @Value("${trendingDailyEndpoint}")
    private String trendingDailyEndpoint;
    @Value("${trendingWeeklyEndpoint}")
    private String trendingWeeklyEndpoint;
    @Value("${trendingMonthlyEndpoint}")
    private String trendingMonthlyEndpoint;
    @Value("${trendingYearlyEndpoint}")
    private String trendingYearlyEndpoint;
    @Value("${trendingForeverEndpoint}")
    private String trendingForeverEndpoint;

    private final static HashMap<String, URL> urlsHashMap = new HashMap<>();

    private void loadUrls() throws MalformedURLException {
        if (urlsHashMap.isEmpty()) {
            urlsHashMap.put("now", new URL(hostname + trendingNowEndpoint));
            urlsHashMap.put("daily", new URL(hostname + trendingDailyEndpoint));
            urlsHashMap.put("weekly", new URL(hostname + trendingWeeklyEndpoint));
            urlsHashMap.put("monthly", new URL(hostname + trendingMonthlyEndpoint));
            urlsHashMap.put("yearly", new URL(hostname + trendingYearlyEndpoint));
            urlsHashMap.put("forever", new URL(hostname + trendingForeverEndpoint));
            log.info("loaded URLs are {}", urlsHashMap);
        } else {
            log.info("URLs are already loaded.");
        }
    }

    public URL getUrl(String trendingType) throws MalformedURLException {
        loadUrls();
        return urlsHashMap.get(trendingType);
    }
}
