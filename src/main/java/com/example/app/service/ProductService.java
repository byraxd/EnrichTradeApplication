package com.example.app.service;

import com.example.app.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<Product> uploadProducts(MultipartFile file);
}
