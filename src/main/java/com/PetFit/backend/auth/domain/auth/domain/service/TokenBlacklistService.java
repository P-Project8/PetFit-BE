package com.PetFit.backend.auth.domain.auth.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "BLACKLIST:";

    public boolean isBlacklistToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }

    public void blacklist(String token, Duration expiration) {
        redisTemplate.opsForValue().set(PREFIX + token, "true", expiration);
    }
}
