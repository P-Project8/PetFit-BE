package com.PetFit.backend.auth.domain.email.domain.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String VERIFICATION_PREFIX = "EMAIL_VERIFIED:";
    private static final String VERIFICATION_CODE_PREFIX = "EMAIL_VERIFICATION_CODE:";
    private static final String VERIFICATION_ATTEMPT_PREFIX = "EMAIL_VERIFICATION_ATTEMPT:";

    private static final long VERIFICATION_CODE_TTL_SECONDS = 300;
    private static final int MAX_ATTEMPT_COUNT = 5;
    private static final long ATTEMPT_TTL_SECONDS = 600;

    public void markEmailAsVerified(String email, long ttlSeconds) {
        String key = VERIFICATION_PREFIX + email;
        redisTemplate.opsForValue().set(key, "true", Duration.ofSeconds(ttlSeconds));
        log.info("이메일 인증 상태 저장: {}, TTL: {}초", email, ttlSeconds);
    }

    public boolean isEmailVerified(String email) {
        String key = VERIFICATION_PREFIX + email;
        String verified = redisTemplate.opsForValue().get(key);
        return "true".equals(verified);
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void saveVerificationCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofSeconds(VERIFICATION_CODE_TTL_SECONDS));
        log.info("이메일 인증 코드 저장: {}, TTL: {}초", email, VERIFICATION_CODE_TTL_SECONDS);
    }

    public boolean verifyCode(String email, String inputCode) {
        if (isMaxAttemptsExceeded(email)) {
            log.warn("이메일 인증 시도 횟수 초과: {}", email);
            throw new RuntimeException("인증 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
        }

        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            log.warn("이메일 인증 코드가 존재하지 않음: {}", email);
            incrementAttemptCount(email);
            return false;
        }

        if (storedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            redisTemplate.delete(VERIFICATION_ATTEMPT_PREFIX + email);
            log.info("이메일 인증 코드 검증 성공: {}", email);
            return true;
        }

        log.warn("이메일 인증 코드 불일치: {}", email);
        incrementAttemptCount(email);
        return false;
    }

    public void removeVerificationCode(String email) {
        redisTemplate.delete(VERIFICATION_CODE_PREFIX + email);
    }

    public int getRemainingAttemptCount(String email) {
        String key = VERIFICATION_ATTEMPT_PREFIX + email;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        return Math.max(0, MAX_ATTEMPT_COUNT - count);
    }

    public void removeEmailVerification(String email) {
        redisTemplate.delete(VERIFICATION_PREFIX + email);
    }

    private void incrementAttemptCount(String email) {
        String key = VERIFICATION_ATTEMPT_PREFIX + email;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(ATTEMPT_TTL_SECONDS));
        }
    }

    private boolean isMaxAttemptsExceeded(String email) {
        String key = VERIFICATION_ATTEMPT_PREFIX + email;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        return count >= MAX_ATTEMPT_COUNT;
    }
}
