package com.example.app.parser.service;

import com.example.app.entity.Product;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

public interface ProductParserService {
    Flux<Product> parse(MultipartFile file);
}
