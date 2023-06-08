package com.hw.cloudstorage.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("BlockedToken")
public class BlockedToken implements Serializable {
    @Id
    private String token;
    @TimeToLive
    private Long expiration;
}
