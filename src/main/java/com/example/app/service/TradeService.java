package com.example.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

public interface TradeService {

    ByteArrayInputStream getTrade(MultipartFile file);
}
