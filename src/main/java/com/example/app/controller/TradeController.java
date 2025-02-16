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
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    /**
     * @param file - csv file, that should be .csv, and should contain values date, productId, currency, price.
     * @return - Modified file
     */
    @PostMapping(value = "/enrich", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "text/csv")
    public Mono<ResponseEntity<byte[]>> enrichCSV(@RequestParam("file") MultipartFile file) {
        return tradeService.getTrade(file)
                .map(data -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.valueOf("text/csv"));
                    headers.setContentDispositionFormData("attachment", "trade.csv");

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(data);
                });
    }
}
