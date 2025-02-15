package com.example.app.parser;

import com.example.app.parser.service.ProductParserService;
import com.example.app.parser.service.impl.CsvProductParserServiceImpl;
import com.example.app.parser.service.impl.JsonProductParserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductParser {

    @Autowired
    private CsvProductParserServiceImpl csvParser;
    @Autowired
    private JsonProductParserServiceImpl jsonParser;

    public ProductParserService getParser(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName.endsWith(".csv")) {
            return csvParser;
        }
        if (fileName.endsWith(".json")) {
            return jsonParser;
        }
        throw new IllegalArgumentException("Unsupported file type");
    }
}
