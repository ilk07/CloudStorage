package com.hw.cloudstorage.services;

import com.hw.cloudstorage.model.entity.BlockedToken;

public interface TokenService {
    boolean isBlocked(String token);
    BlockedToken save(BlockedToken blockedToken);
}
