package com.PetFit.backend.auth.domain.auth.domain.service;

import com.PetFit.backend.auth.domain.auth.domain.entity.TokenBlacklist;
import com.PetFit.backend.auth.domain.auth.domain.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Transactional(readOnly = true)
    public boolean isBlacklistToken(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public void blacklist(String token, Duration expiration) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(expiration);
        tokenBlacklistRepository.save(TokenBlacklist.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build());
    }
}
