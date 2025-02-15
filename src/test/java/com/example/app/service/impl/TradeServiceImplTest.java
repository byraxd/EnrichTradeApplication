package com.example.app.service.impl;

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
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TradeServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @InjectMocks
    private TradeServiceImpl tradeService;

    String tradeCsv = "date,productId,currency,price\n20250215,1,EUR,10\n20250214,2,GBP,35";

    @BeforeEach
    public void setUp() {
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Mockito.lenient().when(valueOperations.get("product: 1")).thenReturn(Mono.just("FirstProduct"));
        Mockito.lenient().when(valueOperations.get("product: 2")).thenReturn(Mono.just("SecondProduct"));
    }

    @Test
    void test_getTrade_whenCsvFileIsValid() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(tradeCsv.getBytes());

        ByteArrayInputStream result = tradeService.getTrade(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(result));
        List<String> lines = new ArrayList<>();

        lines.add("date,productName,currency,price");
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        List<String> excepted = List.of(
                "date,productName,currency,price",
                "20250215,FirstProduct,EUR,10",
                "20250214,SecondProduct,GBP,35"
        );

        Assertions.assertEquals(excepted, lines);

        Mockito.verify(valueOperations, Mockito.times(2)).get(Mockito.anyString());
        Mockito.verify(redisTemplate, Mockito.times(2)).opsForValue();
    }

    @Test
    void test_getTrade_whenCsvFileIsEmpty() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("".getBytes());

        ByteArrayInputStream result = tradeService.getTrade(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(result));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        List<String> excepted = List.of();
        Assertions.assertEquals(excepted, lines);
        Mockito.verify(redisTemplate, Mockito.times(0)).opsForValue();
    }
}
