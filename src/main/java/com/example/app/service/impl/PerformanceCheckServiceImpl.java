package com.example.app.service.impl;

import com.example.app.service.PerformanceCheckService;
import com.example.app.service.ProductService;
import com.example.app.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class PerformanceCheckServiceImpl implements PerformanceCheckService {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ProductService productService;

    @Override
    public void performanceCheckForFileWithProducts(MultipartFile file) {
        Instant startTime = Instant.now();
        log.info("Starting performance check for product file at {}", startTime);

        productService.uploadProducts(file)
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(product -> log.debug("Processed product: {}", product))
                .sequential()
                .then()
                .doOnSuccess(done -> logPerformance(startTime, "Product Upload"))
                .subscribe();
    }

    @Override
    public void performanceCheckForFileWithTrades(MultipartFile file) {
        Instant startTime = Instant.now();
        log.info("Starting performance check for trade file at {}", startTime);

        Mono.fromCallable(() -> tradeService.getTrade(file))
                .subscribeOn(Schedulers.boundedElastic()) // Offload to separate thread
                .doOnSuccess(data -> log.info("Trade processing completed successfully"))
                .doOnError(error -> log.error("Error processing trade file: {}", error.getMessage()))
                .doFinally(signal -> logPerformance(startTime, "Trade Processing"))
                .subscribe();

    }

    private void logPerformance(Instant startTime, String operation) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("{} completed in {} ms", operation, duration.toMillis());
    }
}
