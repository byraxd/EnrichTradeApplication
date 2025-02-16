package com.example.app.service.impl;

import com.example.app.entity.Product;
import com.example.app.parser.ProductParser;
import com.example.app.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    private ProductParser productParser;

    /**
     * @param file - csv file, that should be .csv, and should contain values productId and productName.
     * @return - returning uploaded to redis database list of parsed products from .csv file
     */
    @Override
    public Flux<Product> uploadProducts(MultipartFile file) {

        log.info("Uploading products to redis database");

        return productParser.getParser(file)
                .parse(file)
                .flatMap(product -> {
                    String key = "product:" + product.getProductId();
                    return redisTemplate.opsForValue()
                            .set(key, product.getProductName())
                            .doOnSuccess(success -> log.info("Successfully cached product: {} -> {}", key, product.getProductName()))
                            .doOnError(error -> log.error("Error caching product: {} -> {}", key, product.getProductName()))
                            .thenReturn(product);
                });
    }

}
