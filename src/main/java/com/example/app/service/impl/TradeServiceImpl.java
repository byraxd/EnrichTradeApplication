package com.example.app.service.impl;

import com.example.app.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
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

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     *
     * @param inputStream - converted from Multipart file inputStream, that contains all information from .csv file
     * @return - Verified and modified ByteArrayInputStream from .csv file
     */
    @Override
    public ByteArrayInputStream getTrade(InputStream inputStream) {
        Flux<String> lines = Flux.create(sink -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    sink.next(line);
                }
                sink.complete();
            } catch (IOException e) {
                sink.error(e);
            }
        });

        List<String> records = new ArrayList<>();
        records.add("date,productName,currency,price");

        return lines
                .skip(1)
                .filter(line -> !line.isEmpty())
                .map(line -> validateLine(line))
                .collectList()
                .map(this::convertListLinesIntoByteArrayInputStream)
                .block();
    }

    private String validateLine(String line) {
        // converting a line into Array of string, that will contain {"date", "productId","currency", "price"}
        String[] record = line.split(",");
        if(record.length != 4) {
            log.error("Invalid record: Excepted 4 columns, but got {}", record.length);
            return null;
        }

        String date = record[0];
        String productId = record[1];
        String currency = record[2];
        String price = record[3];

        // Validating date format
        validateDateFormat(date, line);

        // Getting from redis productName by key productId,
        // and modifying value, if productName is missing by following key
        String key = "product: " + productId;
        Mono<String> productName = getNameByKeyAndModifyIfMissingProductName(key);

        return productName.map(name -> String.join(",", date, name, currency, price)).block();
    }

    /**
     *
     * @param key - String key with format "product: {productId}"
     * @return - name that method taking by key value in redisTemplate,
     * and if Product name is Missing or contains null value, method converting it into "Missing Product Name"
     */
    private Mono<String> getNameByKeyAndModifyIfMissingProductName(String key){
        return redisTemplate.opsForValue().get(key)
                .defaultIfEmpty("Missing Product Name")
                .doOnError(e -> log.error("Missing product name for key: {}, {}", key, e.getMessage()));
    }

    /**
     *
     * @param lines - List that contains lines of csv file
     * @return - returning converted list lines into ByteArrayInputStream
     */
    private ByteArrayInputStream convertListLinesIntoByteArrayInputStream(List<String> lines) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try(PrintWriter printWriter = new PrintWriter(outputStream)) {
            for(String line : lines) {
                printWriter.println(line);
            }
            printWriter.flush();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Method validating date format with options "yyyyMMdd"
     * and converting it to invalidDate in case if date format is invalid
     *
     * @param date - String date with format "yyyyMMdd"
     * @param line - String line which one contains full size of trade values "date,productId,currency,price"
     */
    private void validateDateFormat(String date, String line){
        try {
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException ex) {
            log.error("Invalid date format for record '{}': {}", line, ex.getMessage());
        }
    }
}
