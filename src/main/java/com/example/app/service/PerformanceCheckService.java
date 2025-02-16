package com.example.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface PerformanceCheckService {
    void performanceCheckForFileWithProducts(MultipartFile file);

    void performanceCheckForFileWithTrades(MultipartFile file);

}
