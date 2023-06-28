package com.hw.cloudstorage;

import com.hw.cloudstorage.config.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration(RedisProperties redisProperties) {
        RedisServer redisServer = RedisServer.builder()
                .port(redisProperties.getPort())
                .setting("requirepass " + redisProperties.getPassword())
                .build();
        this.redisServer = redisServer;
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
