package com.example.app.service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

public interface TradeService {

    Mono<byte[]> getTrade(MultipartFile file);
}
