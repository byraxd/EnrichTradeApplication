package com.example.app.consumer;

import com.example.app.entity.Trade;
import com.example.app.service.RecordBatchInFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Configuration
@Slf4j
public class BatchTradeConsumer {

    @Bean
    public Consumer<List<Trade>> tradeConsumer(RecordBatchInFileService recordBatchInFileService){
        return trades -> {
            log.info("Received {} trades from file", trades.size());
            recordBatchInFileService.appendTrades(trades);
        };
    }
}
