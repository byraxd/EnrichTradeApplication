package com.example.app.parser;

import com.example.app.parser.service.TradeParserService;
import com.example.app.parser.service.impl.CsvTradeParserServiceImpl;
import com.example.app.parser.service.impl.JsonTradeParserServiceImpl;
import com.example.app.parser.service.impl.XmlTradeProductParserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class TradeParser {

    Map<String, TradeParserService> map;

    @Autowired
    public TradeParser(CsvTradeParserServiceImpl csvParser, JsonTradeParserServiceImpl jsonParser, XmlTradeProductParserServiceImpl xmlParser) {
        map = new HashMap<>();

        map.put(".csv", csvParser);
        map.put(".json", jsonParser);
        map.put(".xml", xmlParser);
    }

    public TradeParserService getParser(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if(file.isEmpty())throw new IllegalArgumentException("Unsupported file format");

        return map.entrySet().stream()
                .filter(entry -> filename.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type"));
    }
}
