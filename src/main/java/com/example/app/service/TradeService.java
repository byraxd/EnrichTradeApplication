package com.example.app.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface TradeService {

    ByteArrayInputStream getTrade(InputStream inputStream);
}
