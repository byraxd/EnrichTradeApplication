package com.example.app.service.impl;

import com.example.app.csvRepresentation.ProductRepresentation;
import com.example.app.entity.Product;
import com.example.app.service.ProductService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    /**
     * @param file - csv file, that should be .csv, and should contain values productId and productName.
     * @return - returning uploaded to redis database list of parsed products from .csv file
     */
    @Override
    public Flux<Product> uploadProducts(MultipartFile file) {
        log.info("Uploading products to redis database");

        return parseCsv(file)
                .flatMap(product -> {
                    String key = "product:" + product.getProductId();
                    return redisTemplate.opsForValue()
                            .set(key, product.getProductName())
                            .doOnSuccess(success -> log.info("Successfully cached product: {} -> {}", key, product.getProductName()))
                            .doOnError(error -> log.error("Error caching product: {} -> {}", key, product.getProductName()))
                            .thenReturn(product);
                });
    }

    /**
     * @param file - csv file, that should be .csv, and should contain values productId and productName.
     * @return - returning List of parsed products from .csv file
     */
    private Flux<Product> parseCsv(MultipartFile file) {
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
