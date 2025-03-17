package com.example.app.service;

import com.example.app.entity.Trade;

import java.util.List;

public interface RecordBatchInFileService {

    void appendTrades(List<Trade> trades);
}
