package com.PetFit.backend.auth.domain.auth.domain.service;

import com.PetFit.backend.auth.domain.auth.domain.entity.RefreshToken;
import com.PetFit.backend.auth.domain.auth.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String userId, String refreshToken, Duration timeout) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(timeout);
        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existing -> existing.update(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(userId)
                                .token(refreshToken)
                                .expiresAt(expiresAt)
                                .build())
                );
    }

    public void deleteRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public String findByUserId(String userId) {
        return refreshTokenRepository.findByUserId(userId)
                .filter(rt -> !rt.isExpired())
                .map(RefreshToken::getToken)
                .orElse(null);
    }

    public boolean isExist(String token, String userId) {
        return refreshTokenRepository.findByUserId(userId)
                .filter(rt -> !rt.isExpired())
                .map(rt -> rt.getToken().equals(token))
                .orElse(false);
    }
}
