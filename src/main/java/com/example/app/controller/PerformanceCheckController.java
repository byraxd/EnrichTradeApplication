package com.example.app.controller;

import com.example.app.service.PerformanceCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/performance")
public class PerformanceCheckController {

    @Autowired
    private PerformanceCheckService performanceCheckService;

    @GetMapping("/products")
    public ResponseEntity<String> checkPerformanceForFileWithProducts(@RequestParam MultipartFile file) {
        performanceCheckService.performanceCheckForFileWithProducts(file);
        return ResponseEntity.ok("Success performance check for products file");
    }
    @GetMapping("/trades")
    public ResponseEntity<String> checkPerformanceForFileWithTrades(@RequestParam MultipartFile file) {
        performanceCheckService.performanceCheckForFileWithTrades(file);
        return ResponseEntity.ok("Success performance check for trades file");
    }
}
