package com.example.app.parser;

import com.example.app.parser.service.TradeParserService;
import com.example.app.parser.service.impl.CsvTradeParserServiceImpl;
import com.example.app.parser.service.impl.JsonTradeParserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TradeParser {

    @Autowired
    private CsvTradeParserServiceImpl csvParser;

    @Autowired
    private JsonTradeParserServiceImpl jsonParser;

    public TradeParserService getParser(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename.endsWith(".csv")) {
            return csvParser;
        }
        if (filename.endsWith(".json")) {
            return jsonParser;
        }
        throw new IllegalArgumentException("Unsupported file format");
    }
}
