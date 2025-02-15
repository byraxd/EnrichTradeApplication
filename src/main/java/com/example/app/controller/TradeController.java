package com.example.app.controller;

import com.example.app.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping(value = "/enrich", consumes = "text/csv", produces = "text/csv")
    public ResponseEntity<byte[]> enrichCSV(@RequestParam("file") byte[] file) {
        ByteArrayInputStream inputStream = tradeService.getTrade(new ByteArrayInputStream(file));
        byte[] data = inputStream.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/csv"));
        headers.setContentDispositionFormData("attachment", "trade.csv");

        return ResponseEntity.ok().headers(headers).body(data);
    }
}
