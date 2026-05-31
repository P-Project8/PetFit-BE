package com.PetFit.backend.subscription.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.SubscriptionErrorStatus;
import com.PetFit.backend.subscription.domain.entity.Subscription;
import com.PetFit.backend.subscription.domain.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * 사용자의 현재 ACTIVE 구독을 조회.
     * 없으면 FREE 구독을 즉시 생성하여 반환 (lazy bootstrap).
     */
    public Subscription getOrCreateActive(String userId) {
        return subscriptionRepository
                .findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, Subscription.STATUS_ACTIVE)
                .orElseGet(() -> createFree(userId));
    }

    public Subscription createFree(String userId) {
        Subscription free = Subscription.builder()
                .userId(userId)
                .plan(Subscription.PLAN_FREE)
                .status(Subscription.STATUS_ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(null)
                .build();
        return subscriptionRepository.save(free);
    }

    public Subscription upgradeToPremium(String userId, int days) {
        Subscription active = getOrCreateActive(userId);
        if (Subscription.PLAN_PREMIUM.equals(active.getPlan()) && active.isPremium()) {
            throw new RestApiException(SubscriptionErrorStatus.ALREADY_PREMIUM);
        }
        // 기존 ACTIVE는 만료 처리 후 새 PREMIUM 생성
        active.cancel();
        subscriptionRepository.save(active);

        Subscription premium = Subscription.builder()
                .userId(userId)
                .plan(Subscription.PLAN_PREMIUM)
                .status(Subscription.STATUS_ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(days))
                .build();
        return subscriptionRepository.save(premium);
    }

    public Subscription cancel(String userId) {
        Subscription active = getOrCreateActive(userId);
        if (Subscription.PLAN_FREE.equals(active.getPlan())) {
            throw new RestApiException(SubscriptionErrorStatus.CANNOT_CANCEL_FREE);
        }
        active.cancel();
        subscriptionRepository.save(active);

        // 취소 후에는 즉시 FREE 활성화
        return createFree(userId);
    }

    public List<Subscription> findExpiredPremiums() {
        return subscriptionRepository.findExpiredPremiums(LocalDateTime.now());
    }

    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}
