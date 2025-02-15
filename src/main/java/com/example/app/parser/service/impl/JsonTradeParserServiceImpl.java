package com.example.app.parser.service.impl;

import com.example.app.entity.Trade;
import com.example.app.parser.service.TradeParserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class JsonTradeParserServiceImpl implements TradeParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Method taking .json file and parsing information from him
     *
     * @param file - .json file
     * @return - Flux<Trade> flux of parsed from file trades
     */
    @Override
    public Flux<Trade> parse(MultipartFile file) {
        try {
            List<Trade> trades = objectMapper.readValue(
                    file.getInputStream(), new TypeReference<>() {
                    });
            return Flux.fromIterable(trades);
        } catch (IOException e) {
            log.error("Error parsing JSON file: {}", e.getMessage());
            return Flux.empty();
        }
    }
}
