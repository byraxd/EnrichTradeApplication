package com.example.app.service.impl;

import com.example.app.entity.Product;
import com.example.app.parser.ProductParser;
import com.example.app.parser.service.ProductParserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ProductParser productParser;

    @Mock
    private ProductParserService productParserService;

    @InjectMocks
    private ProductServiceImpl productService;

    String productCsv = "productId,productName\n1,FirstProduct\n2,SecondProduct";

    MultipartFile file;

    @BeforeEach
    public void setUp() {
        file = new MockMultipartFile(
                "product.csv",
                "product.csv",
                "text/csv",
                productCsv.getBytes());

        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.lenient().when(valueOperations.set(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());
    }

    @Test
    void test_uploadProducts_whenCsvFileIsValid() {
        Product product1 = Product.builder().productId(1).productName("FirstProduct").build();
        Product product2 = Product.builder().productId(2).productName("SecondProduct").build();

        Mockito.when(productParser.getParser(file)).thenReturn(productParserService);
        Mockito.when(productParserService.parse(file)).thenReturn(Flux.just(product1, product2));

        Flux<Product> products = productService.uploadProducts(file);

        Assertions.assertNotNull(products);
        Assertions.assertEquals(2, products.count().block());

        Mockito.verify(redisTemplate, Mockito.times(2)).opsForValue();
        Mockito.verify(valueOperations, Mockito.times(2)).set(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void test_uploadProducts_whenCsvFileIsEmpty() {
        file = new MockMultipartFile(
                "product.csv",
                "product.csv",
                "text/csv",
                "".getBytes());

        Mockito.when(productParser.getParser(file)).thenReturn(productParserService);
        Mockito.when(productParserService.parse(file)).thenReturn(Flux.empty());

        Flux<Product> products = productService.uploadProducts(file);

        Assertions.assertNotNull(products);
        Assertions.assertEquals(0, products.count().block());

        Mockito.verify(redisTemplate, Mockito.times(0)).opsForValue();
        Mockito.verify(valueOperations, Mockito.times(0)).set(Mockito.anyString(), Mockito.anyString());
    }
}