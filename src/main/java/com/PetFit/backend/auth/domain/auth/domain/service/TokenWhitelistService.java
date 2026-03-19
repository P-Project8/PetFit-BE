package com.PetFit.backend.auth.domain.auth.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenWhitelistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "WHITELIST:";

    public boolean isWhitelistToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }

    public void whitelist(String token, Duration timeout) {
        redisTemplate.opsForValue().set(PREFIX + token, "true", timeout);
    }

    public void deleteWhitelistToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
