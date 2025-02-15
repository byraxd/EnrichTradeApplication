package com.example.app.service.impl;

import com.example.app.entity.Trade;
import com.example.app.parser.TradeParser;
import com.example.app.parser.service.TradeParserService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TradeServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private TradeParser tradeParser;

    @Mock
    private TradeParserService tradeParserService;

    @InjectMocks
    private TradeServiceImpl tradeService;

    private final String tradeCsv = "date,productId,currency,price\n20250215,1,EUR,10\n20250214,2,GBP,35";

    @BeforeEach
    public void setUp() {
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Mockito.lenient().when(valueOperations.get("product:1")).thenReturn(Mono.just("FirstProduct"));
        Mockito.lenient().when(valueOperations.get("product:2")).thenReturn(Mono.just("SecondProduct"));
    }

    @Test
    void test_getTrade_whenCsvFileIsValid() {
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv",
                tradeCsv.getBytes(StandardCharsets.UTF_8));

        Trade trade1 = Trade.builder()
                .date("20250215")
                .productId(1)
                .currency("EUR")
                .price("10")
                .build();
        Trade trade2 = Trade.builder()
                .date("20250214")
                .productId(2)
                .currency("GBP")
                .price("35")
                .build();

        Mockito.when(tradeParser.getParser(file)).thenReturn(tradeParserService);
        Mockito.when(tradeParserService.parse(file)).thenReturn(Flux.just(trade1, trade2));

        ByteArrayInputStream result = tradeService.getTrade(file);

        BufferedReader reader = new BufferedReader(new InputStreamReader(result, StandardCharsets.UTF_8));
        List<String> lines = new ArrayList<>(reader.lines().toList());

        List<String> expected = List.of(
                "date,productName,currency,price",
                "20250215,FirstProduct,EUR,10",
                "20250214,SecondProduct,GBP,35"
        );

        Assertions.assertEquals(expected, lines);

        Mockito.verify(valueOperations, Mockito.times(1)).get("product:1");
        Mockito.verify(valueOperations, Mockito.times(1)).get("product:2");
        Mockito.verify(redisTemplate, Mockito.times(2)).opsForValue();
    }

    @Test
    void test_getTrade_whenCsvFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv",
                "".getBytes(StandardCharsets.UTF_8));

        Mockito.when(tradeParser.getParser(file)).thenReturn(tradeParserService);
        Mockito.when(tradeParserService.parse(file)).thenReturn(Flux.empty());

        ByteArrayInputStream result = tradeService.getTrade(file);

        BufferedReader reader = new BufferedReader(new InputStreamReader(result, StandardCharsets.UTF_8));
        List<String> lines = new ArrayList<>(reader.lines().toList());

        List<String> expected = List.of("date,productName,currency,price");

        Assertions.assertEquals(expected, lines);
        Mockito.verify(redisTemplate, Mockito.never()).opsForValue();
    }
}