package com.PetFit.backend.subscription.domain.service;

import com.PetFit.backend.ai.domain.repository.AiStylingRepository;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.SubscriptionErrorStatus;
import com.PetFit.backend.subscription.domain.entity.Subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * AI 스타일링 크레딧(사용량) 관리.
 *
 * - FREE 플랜: 월 {@link #FREE_MONTHLY_LIMIT}회 제한
 * - PREMIUM 플랜: 무제한
 *
 * 사용량은 AiStyling 엔티티의 이번 달 카운트로 계산하므로
 * 별도 카운터 테이블 동기화 문제가 없다.
 */
@Service
@RequiredArgsConstructor
@Transactional // readOnly=false: subscriptionService.getOrCreateActive()가 최초 호출 시 FREE INSERT
public class CreditService {

    public static final int FREE_MONTHLY_LIMIT = 3;
    public static final int PREMIUM_MONTHLY_LIMIT = Integer.MAX_VALUE;

    private final AiStylingRepository aiStylingRepository;
    private final SubscriptionService subscriptionService;

    public CreditStatus getStatus(String userId) {
        Subscription sub = subscriptionService.getOrCreateActive(userId);
        long used = countThisMonth(userId);
        int limit = sub.isPremium() ? PREMIUM_MONTHLY_LIMIT : FREE_MONTHLY_LIMIT;
        long remaining = sub.isPremium() ? Long.MAX_VALUE : Math.max(0, limit - used);
        return new CreditStatus(sub.getPlan(), used, limit, remaining, sub.isPremium());
    }

    /**
     * 크레딧 사용 가능 여부 확인 — AI 호출 직전에 호출.
     * 사용 한도 초과 시 RestApiException 발생.
     */
    public void assertCanConsume(String userId) {
        Subscription sub = subscriptionService.getOrCreateActive(userId);
        if (sub.isPremium()) return;

        long used = countThisMonth(userId);
        if (used >= FREE_MONTHLY_LIMIT) {
            throw new RestApiException(SubscriptionErrorStatus.CREDIT_LIMIT_EXCEEDED);
        }
    }

    private long countThisMonth(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();
        return aiStylingRepository.countByUserIdInPeriod(userId, start, end);
    }

    public record CreditStatus(
            String plan,
            long used,
            int monthlyLimit,
            long remaining,
            boolean unlimited
    ) {}
}
