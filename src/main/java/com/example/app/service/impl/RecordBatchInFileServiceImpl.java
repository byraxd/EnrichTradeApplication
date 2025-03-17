package com.example.app.service.impl;

import com.example.app.entity.Trade;
import com.example.app.service.RecordBatchInFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@Slf4j
public class RecordBatchInFileServiceImpl implements RecordBatchInFileService {

    private final String fileName = "trades.csv";

    public RecordBatchInFileServiceImpl() {
        File file = new File(fileName);

        if(!file.exists()){
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))){
                bufferedWriter.write("date,productName,currency,price");
                bufferedWriter.newLine();
            } catch (IOException e) {
                log.error("Failed initialization of file");
            }
        }
    }

    @Override
    public void appendTrades(List<Trade> trades) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            for(Trade trade: trades){
                String line = String.join(",",
                        trade.getDate(),
                        trade.getProductName(),
                        trade.getCurrency(),
                        trade.getPrice());
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            log.info("In file {} added {} trades ", fileName, trades.size());
        }catch (IOException e){
            log.info("Error in writting in file: {}", e.getMessage());
        }
    }
}
