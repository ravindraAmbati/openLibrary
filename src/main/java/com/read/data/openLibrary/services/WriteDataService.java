package com.read.data.openLibrary.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Log4j2
@Service
public class WriteDataService {

    public String writeDataIntoFile(String trendingType, String response, String fileTypeExtension) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd_MMM_yy");
        String today = LocalDate.now(ZoneId.systemDefault()).format(dateTimeFormatter);
        String fileFullPath = String.join("_", System.getenv("TEMP") + "\\", trendingType, today, fileTypeExtension);
        log.info("Started writing data of trendingType: {}, fileTypeExtension: {} into file full path: {}",
                trendingType, response, fileFullPath);
        FileWriter file;
        try {
            file = new FileWriter(fileFullPath);
            file.write(response);
            file.close();
        } catch (IOException e) {
            log.error("failed to write data into fileFullPath: {}",fileFullPath);
            log.error(e);
        }
        log.info("Completed writing data of trendingType: {}, fileTypeExtension: {} into file full path: {}",
                trendingType, response, fileFullPath);
        return fileFullPath;
    }
}
