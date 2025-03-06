package com.example.app.parser;

import com.example.app.parser.service.ProductParserService;
import com.example.app.parser.service.impl.CsvProductParserServiceImpl;
import com.example.app.parser.service.impl.JsonProductParserServiceImpl;
import com.example.app.parser.service.impl.XmlProductParserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductParser {

    Map<String, ProductParserService> map;

    @Autowired
    public ProductParser(CsvProductParserServiceImpl csvParser, JsonProductParserServiceImpl jsonParser, XmlProductParserServiceImpl xmlParser) {

        map = new HashMap<>();

        map.put(".csv", csvParser);
        map.put(".json", jsonParser);
        map.put(".xml", xmlParser);
    }

    public ProductParserService getParser(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if(fileName.isEmpty()) throw new IllegalArgumentException("Filename cannot be empty");

        return map.entrySet().stream()
                .filter(entry -> fileName.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type"));
    }
}
