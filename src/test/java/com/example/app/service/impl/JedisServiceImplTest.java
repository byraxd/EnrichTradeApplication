package com.example.app.service.impl;

import com.example.app.parser.service.impl.JedisServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JedisServiceImplTest {

    @Autowired
    private JedisServiceImpl jedisService;


    @Test
    void test_saveKeyValueAndGetValueByKey() {
        String statusCodeForFirstValue = jedisService.saveKeyValue("first key", "first value");
        String statusCodeForSecondValue = jedisService.saveKeyValue("second key", "second value");

        String firstResult = jedisService.getValueByKey("first key");
        String secondResult = jedisService.getValueByKey("second key");

        Assertions.assertEquals("OK", statusCodeForFirstValue);
        Assertions.assertEquals("OK", statusCodeForSecondValue);

        Assertions.assertEquals("first value", firstResult);
        Assertions.assertEquals("second value", secondResult);
    }
}
