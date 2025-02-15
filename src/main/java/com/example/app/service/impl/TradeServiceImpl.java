package com.example.app.service.impl;

import com.example.app.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class TradeServiceImpl implements TradeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public ByteArrayInputStream getTrade(InputStream inputStream) {
        List<String> records = new ArrayList<>();

        records.add("date,productId,currency,price");

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String header = reader.readLine();
            if(header == null || header.isEmpty()) {
                log.error("This line is empty, or contains null value");
                return new ByteArrayInputStream("".getBytes());
            }

            String line;
            while((line = reader.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }

                String[] record = line.split(",");
                if(record.length != 4) {
                    log.error("Invalid record: Excepted 4 columns, but got {}", record.length);
                    continue;
                }

                String date = record[0];
                String productId = record[1];
                String currency = record[2];
                String price = record[3];

                validateDateFormat(date, line);

                String key = "product: " +productId;
                String productName = redisTemplate.opsForValue().get(key);

                if(productName == null || productName.isEmpty()) {
                    log.error("Missing product name for id: {}", productId);
                    productName = "Missing Product Name";
                }

                String enrichedLine = String.join(",", date, productName, currency, price);
                records.add(enrichedLine);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return convertListLinesIntoByteArrayInputStream(records);
    }

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

    private void validateDateFormat(String date, String line){
        try {
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException ex) {
            log.error("Invalid date format for record '{}': {}", line, ex.getMessage());
        }
    }
}
