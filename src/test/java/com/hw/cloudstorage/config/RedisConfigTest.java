package com.hw.cloudstorage.config;

import com.hw.cloudstorage.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RedisConfigTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void redisTemplate_shouldReturnPONGStringAfterTestConnection() {

        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        String expected = "PONG";
        String actual = connection.ping();
        assertEquals(expected, actual);

    }
}