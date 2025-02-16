package com.example.app.service;

public interface JedisService {

    String setValueByKey(String key, String value);
    String getValueByKey(String key);
}
