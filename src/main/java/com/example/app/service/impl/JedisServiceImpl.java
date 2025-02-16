package com.example.app.service.impl;

import com.example.app.service.JedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class JedisServiceImpl implements JedisService {

    @Autowired
    private Jedis jedis;

    @Override
    public String setValueByKey(String key, String value) {
        return jedis.set(key, value);
    }

    @Override
    public String getValueByKey(String key) {
        return jedis.get(key);
    }
}
