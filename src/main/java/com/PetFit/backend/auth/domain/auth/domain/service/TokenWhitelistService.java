package com.PetFit.backend.auth.domain.auth.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenWhitelistService {

    // 인메모리 캐시 (서버 재시작 시 초기화 - 학교 프로젝트 수준에서 충분)
    private final Map<String, Long> whitelist = new ConcurrentHashMap<>();

    public boolean isWhitelistToken(String token) {
        Long expiresAt = whitelist.get(token);
        if (expiresAt == null) return false;
        if (System.currentTimeMillis() > expiresAt) {
            whitelist.remove(token);
            return false;
        }
        return true;
    }

    public void whitelist(String token, Duration timeout) {
        whitelist.put(token, System.currentTimeMillis() + timeout.toMillis());
    }

    public void deleteWhitelistToken(String token) {
        whitelist.remove(token);
    }
}
