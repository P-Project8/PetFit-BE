package com.PetFit.backend.auth.domain.auth.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "REFRESH_TOKEN:";

    public void saveRefreshToken(String userId, String refreshToken, Duration timeout) {
        redisTemplate.opsForValue().set(PREFIX + userId, refreshToken, timeout);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    public String findByUserId(String userId) {
        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public boolean isExist(String token, String userId) {
        String storedToken = findByUserId(userId);
        return token.equals(storedToken);
    }
}
