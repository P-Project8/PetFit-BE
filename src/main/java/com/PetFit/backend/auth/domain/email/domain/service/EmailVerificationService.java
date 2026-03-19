package com.PetFit.backend.auth.domain.email.domain.service;

import com.PetFit.backend.auth.domain.auth.domain.entity.EmailVerification;
import com.PetFit.backend.auth.domain.auth.domain.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;

    private static final long VERIFICATION_CODE_TTL_SECONDS = 300;
    private static final int MAX_ATTEMPT_COUNT = 5;

    public void markEmailAsVerified(String email, long ttlSeconds) {
        emailVerificationRepository.findByEmail(email)
                .ifPresentOrElse(
                        EmailVerification::markVerified,
                        () -> emailVerificationRepository.save(EmailVerification.builder()
                                .email(email)
                                .verified(true)
                                .expiresAt(LocalDateTime.now().plusSeconds(ttlSeconds))
                                .build())
                );
        log.info("이메일 인증 상태 저장: {}", email);
    }

    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmail(email)
                .filter(ev -> !ev.isExpired())
                .map(EmailVerification::getVerified)
                .orElse(false);
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void saveVerificationCode(String email, String code) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(VERIFICATION_CODE_TTL_SECONDS);
        emailVerificationRepository.findByEmail(email)
                .ifPresentOrElse(
                        existing -> existing.updateCode(code, expiresAt),
                        () -> emailVerificationRepository.save(EmailVerification.builder()
                                .email(email)
                                .code(code)
                                .expiresAt(expiresAt)
                                .build())
                );
        log.info("이메일 인증 코드 저장: {}", email);
    }

    public boolean verifyCode(String email, String inputCode) {
        EmailVerification ev = emailVerificationRepository.findByEmail(email).orElse(null);

        if (ev == null || ev.isExpired()) {
            log.warn("이메일 인증 코드가 존재하지 않거나 만료: {}", email);
            return false;
        }

        if (ev.isMaxAttemptsExceeded(MAX_ATTEMPT_COUNT)) {
            log.warn("이메일 인증 시도 횟수 초과: {}", email);
            throw new RuntimeException("인증 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
        }

        if (ev.getCode().equals(inputCode)) {
            ev.markVerified();
            log.info("이메일 인증 코드 검증 성공: {}", email);
            return true;
        }

        ev.incrementAttempt();
        log.warn("이메일 인증 코드 불일치: {}", email);
        return false;
    }

    public void removeVerificationCode(String email) {
        emailVerificationRepository.findByEmail(email)
                .ifPresent(ev -> emailVerificationRepository.delete(ev));
    }

    @Transactional(readOnly = true)
    public int getRemainingAttemptCount(String email) {
        return emailVerificationRepository.findByEmail(email)
                .map(ev -> Math.max(0, MAX_ATTEMPT_COUNT - ev.getAttemptCount()))
                .orElse(MAX_ATTEMPT_COUNT);
    }

    public void removeEmailVerification(String email) {
        emailVerificationRepository.findByEmail(email)
                .ifPresent(ev -> emailVerificationRepository.delete(ev));
    }
}
