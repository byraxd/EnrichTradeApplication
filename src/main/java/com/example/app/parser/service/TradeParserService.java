package com.example.app.parser.service;

import com.example.app.entity.Trade;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

public interface TradeParserService {
    Flux<Trade> parse(MultipartFile file);
}
