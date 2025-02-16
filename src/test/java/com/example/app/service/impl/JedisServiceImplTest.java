package com.example.app.service.impl;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class JedisServiceImplTest {

    private static final String REDIS_IMAGE_NAME = "redis:7.0-alpine";
    private static final int REDIS_PORT = 6379;

    @Autowired
    private JedisServiceImpl jedisService;

    static GenericContainer<?> redis;

    @BeforeEach
    void beforeEach() {
        redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE_NAME)).withExposedPorts(REDIS_PORT);
        redis.start();
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", redis.getMappedPort(REDIS_PORT).toString());
    }

    @AfterEach
    void afterEach() {
        redis.stop();
    }

    @Test
    void testSaveKeyValueAndGetValue() {
        String statusCode = jedisService.setValueByKey("key", "value");

        String result = jedisService.getValueByKey("key");

        Assertions.assertEquals("OK", statusCode);
        Assertions.assertEquals("value", result);
    }
}
