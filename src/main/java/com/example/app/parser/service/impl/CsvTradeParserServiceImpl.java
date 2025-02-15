package com.example.app.parser.service.impl;

import com.example.app.csvRepresentation.TradeRepresentation;
import com.example.app.entity.Trade;
import com.example.app.parser.service.TradeParserService;
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
public class CsvTradeParserServiceImpl implements TradeParserService {

    /**
     * Method taking .csv file and parsing information from him
     *
     * @param file - .csv file
     * @return - Flux<Trade> flux of parsed from file trades
     */
    @Override
    public Flux<Trade> parse(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<TradeRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(TradeRepresentation.class);

            CsvToBean<TradeRepresentation> csvToBean = new CsvToBeanBuilder<TradeRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return Flux.fromStream(
                    csvToBean.parse().stream()
                            .map(tr -> Trade.builder()
                                    .date(tr.getDate())
                                    .productId(tr.getProductId())
                                    .currency(tr.getCurrency())
                                    .price(tr.getPrice())
                                    .build())
            );
        } catch (IOException e) {
            log.error("Error parsing CSV file: {}", e.getMessage());
            return Flux.empty();
        }
    }
}
