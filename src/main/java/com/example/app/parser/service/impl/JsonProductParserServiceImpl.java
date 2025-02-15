package com.example.app.parser.service.impl;

import com.example.app.entity.Product;
import com.example.app.parser.service.ProductParserService;
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
public class JsonProductParserServiceImpl implements ProductParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Method taking .json file and parsing information from him
     *
     * @param file - .json file
     * @return - Flux<Product> flux of parsed from file products
     */
    @Override
    public Flux<Product> parse(MultipartFile file) {
        try {
            List<Product> products = objectMapper.readValue(
                    file.getInputStream(), new TypeReference<>() {
                    });
            return Flux.fromIterable(products);
        } catch (IOException e) {
            log.error("IOException parsing JSON file: {}", e.getMessage());
            return Flux.empty();
        }
    }
}
