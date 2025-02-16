package com.example.app.parser.service;

public interface JedisService {

    String saveKeyValue(String key, String value);

    String getValueByKey(String key);
}
