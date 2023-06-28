package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.model.entity.BlockedToken;
import com.hw.cloudstorage.repositories.BlacklistTokenRepository;
import com.hw.cloudstorage.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private BlacklistTokenRepository blackList;

    @Override
    public boolean isBlocked(String token) {
        return blackList.findById(token).isPresent();
    }

    @Override
    public BlockedToken save(BlockedToken blockedToken) {
        return blackList.save(blockedToken);
    }
}
