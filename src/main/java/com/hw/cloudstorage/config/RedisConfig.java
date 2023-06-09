package com.hw.cloudstorage.config;

import com.hw.cloudstorage.model.entity.BlockedToken;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableRedisRepositories
public class RedisConfig {
    @Bean
    public RedisTemplate<String, BlockedToken> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, BlockedToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
