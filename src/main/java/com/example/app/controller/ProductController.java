package com.example.app.controller;

import com.example.app.entity.Product;
import com.example.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * @param file - csv file, that should be .csv, and should contain values productId and productName.
     * @return - returning uploaded to redis database list of parsed products from .csv or .json file
     */
    @PostMapping("/uploads")
    public ResponseEntity<Flux<Product>> uploadProducts(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadProducts(file));
    }
}
