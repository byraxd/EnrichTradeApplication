package com.example.app.service.impl;

import com.example.app.csvRepresentation.ProductRepresentation;
import com.example.app.entity.Product;
import com.example.app.service.ProductService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Product> uploadProducts(MultipartFile file) {
        List<Product> products = parseCsv(file);

        products.forEach(product -> {
            String key = "product: " + product.getProductId();
            redisTemplate.opsForValue().set(key, product.getProductName());
            log.info("Successfully cached product values with key: {}, with value: {}", key, product.getProductName());
        });

        return products;
    }

    private List<Product> parseCsv(MultipartFile file) {
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            HeaderColumnNameMappingStrategy<ProductRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ProductRepresentation.class);

            CsvToBean<ProductRepresentation> csv = new CsvToBeanBuilder<ProductRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csv.parse().stream().map(line -> Product
                    .builder()
                    .productId(line.productId())
                    .productName(line.productName())
                    .build())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }

    }
}
