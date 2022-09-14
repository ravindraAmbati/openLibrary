package com.read.data.openLibrary.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.StringJoiner;

@Log4j2
@Service
public class ReadBookDataService {

    public String getBookDetails(JsonNode bookJsonNode) {
        StringJoiner newLineStrJoiner = new StringJoiner("\r\n");
        String key = String.valueOf(bookJsonNode.get("key")).replaceAll("/works/","");
        String title = String.valueOf(bookJsonNode.get("title")).replaceAll(",", "");
        HashSet<String> authors = getAuthors(bookJsonNode, title);
        String availabilityStatus = getAvailabilityStatus(bookJsonNode);
        HashSet<String> languages = getLanguages(bookJsonNode, title);
        languages.forEach(
                language -> {
                    authors.forEach(
                            author -> {
                                StringJoiner commaStrJoiner = new StringJoiner(",");
                                commaStrJoiner.add(key);
                                commaStrJoiner.add(title);
                                commaStrJoiner.add(author);
                                commaStrJoiner.add(availabilityStatus);
                                commaStrJoiner.add(language);
                                newLineStrJoiner.add(commaStrJoiner.toString());
                            }
                    );
                }
        );
        log.info("book details are title: {}, authors: {}, availability status: {} and languages: {}",
                title, authors, availabilityStatus, languages);
        return newLineStrJoiner.toString();
    }

    public String getAvailabilityStatus(JsonNode bookJsonNode) {
        JsonNode availabilityJsonNode = bookJsonNode.get("availability");
        String availabilityStatus = "UNKNOWN";
        if (null != availabilityJsonNode) {
            availabilityStatus = String.valueOf(availabilityJsonNode.get("status"));
        }
        return availabilityStatus.replaceAll(",", "");
    }

    public HashSet<String> getAuthors(JsonNode bookJsonNode, String title) {
        HashSet<String> authors = new HashSet<>();
        JsonNode authorsJsonNode = bookJsonNode.get("author_name");
        if (null != authorsJsonNode) {
            if (JsonNodeType.ARRAY.equals(authorsJsonNode.getNodeType())) {
                int authorNum = -1;
                JsonNode author;
                while (null != (author = authorsJsonNode.get(++authorNum))) {
                    authors.add(String.valueOf(author).replaceAll(",", ""));
                }
                log.info("authors size: {} for the title: {}", authors.size(), title);
            }
        } else {
            log.warn("authors are empty for title: {}", title);
            return new HashSet<>();
        }
        return authors;
    }

    public HashSet<String> getLanguages(JsonNode bookJsonNode, String title) {
        HashSet<String> languages = new HashSet<>();
        JsonNode languagesJsonNode = bookJsonNode.get("language");
        if (null != languagesJsonNode) {
            if (JsonNodeType.ARRAY.equals(languagesJsonNode.getNodeType())) {
                int languageNum = -1;
                JsonNode language;
                while (null != (language = languagesJsonNode.get(++languageNum))) {
                    String lang = String.valueOf(language)
                            .replaceAll(",", "")
                            .replaceAll("\"","");
                    languages.add(getDisplayLanguage(lang));
                }
                log.info("languages size: {} for the title: {}", languages.size(), title);
            }
        } else {
            log.warn("languages are empty for title: {}", title);
            return new HashSet<>();
        }
        return languages;
    }

    private String getDisplayLanguage(String language){
        Locale locale = Locale.forLanguageTag(language.toUpperCase());
        return locale.getDisplayLanguage();
    }
}
