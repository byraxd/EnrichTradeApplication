package com.example.app.service.impl;

import com.example.app.entity.Trade;
import com.example.app.parser.TradeParser;
import com.example.app.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TradeServiceImpl implements TradeService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private TradeParser tradeParser;

    /**
     * Accepts a file (CSV, JSON, or XML) containing trade records and returns an enriched CSV file.
     * Each trade record is enriched by validating its date format and replacing the product ID with a product name
     * from Redis (or "Missing Product Name" if not found).
     *
     * @param file the input file with trade records (expected fields: date, productId, currency, price)
     * @return a ByteArrayInputStream representing the enriched CSV content.
     */
    @Override
    public ByteArrayInputStream getTrade(MultipartFile file) {

        List<String> csvLines = tradeParser.getParser(file).parse(file)
                .flatMap(this::enrichTrade)
                .collectList()
                .map(this::convertTradesToCsv)
                .block();

        return new ByteArrayInputStream(String.join("\n", csvLines).getBytes());
    }

    /**
     * Enrich a Trade record by validating its date format and retrieving the product name from Redis.
     *
     * @param trade the trade record to enrich.
     * @return a Mono emitting the enriched Trade.
     */
    private Mono<Trade> enrichTrade(Trade trade) {
        validateDateFormat(trade.getDate(), trade);
        String key = "product:" + trade.getProductId();
        return redisTemplate.opsForValue().get(key)
                .defaultIfEmpty("Missing Product Name")
                .doOnError(e -> log.error("Error retrieving product name for key {}: {}", key, e.getMessage()))
                .map(productName -> {
                    trade.setProductName(productName);
                    return trade;
                });
    }

    /**
     * Validates the trade’s date format (expected "yyyyMMdd").
     *
     * @param date  the date string to validate.
     * @param trade the trade record (used for logging context).
     */
    private void validateDateFormat(String date, Trade trade) {
        try {
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException ex) {
            log.error("Invalid date format for trade {}: {}", trade, ex.getMessage());
        }
    }

    /**
     * Converts a list of enriched trades into CSV-formatted lines (with a header).
     *
     * @param trades the list of enriched trades.
     * @return a List of strings representing the CSV lines.
     */
    private List<String> convertTradesToCsv(List<Trade> trades) {
        List<String> lines = new ArrayList<>();

        lines.add("date,productName,currency,price");

        for (Trade trade : trades) {
            String line = String.join(",",
                    trade.getDate(),
                    trade.getProductName(),
                    trade.getCurrency(),
                    trade.getPrice());
            lines.add(line);
        }
        return lines;
    }
}