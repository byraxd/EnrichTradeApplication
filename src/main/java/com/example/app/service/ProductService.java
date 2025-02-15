package com.example.app.service;

import com.example.app.entity.Product;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

public interface ProductService {
    Flux<Product> uploadProducts(MultipartFile file);
}
