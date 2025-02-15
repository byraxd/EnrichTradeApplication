package com.example.app.parser.service.impl;

import com.example.app.csvRepresentation.ProductRepresentation;
import com.example.app.entity.Product;
import com.example.app.parser.service.ProductParserService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Service
@Slf4j
public class CsvProductParserServiceImpl implements ProductParserService {

    /**
     * Method taking .csv file and parsing information from him
     *
     * @param file - .csv file
     * @return - Flux<Product> flux of parsed from file products
     */
    @Override
    public Flux<Product> parse(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<ProductRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ProductRepresentation.class);

            CsvToBean<ProductRepresentation> csv = new CsvToBeanBuilder<ProductRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return Flux.fromStream(
                    csv.parse().stream()
                            .map(line -> Product.builder()
                                    .productId(line.getProductId())
                                    .productName(line.getProductName())
                                    .build())
            );
        } catch (IOException e) {
            log.error("IoException occurred, details: {}", e.getMessage());
            return Flux.empty();
        }
    }
}
