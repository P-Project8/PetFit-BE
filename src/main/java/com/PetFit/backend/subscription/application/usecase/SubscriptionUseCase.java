package com.PetFit.backend.subscription.application.usecase;

import com.PetFit.backend.subscription.domain.entity.Subscription;
import com.PetFit.backend.subscription.domain.service.CreditService;
import com.PetFit.backend.subscription.domain.service.SubscriptionService;
import com.PetFit.backend.subscription.presentation.dto.response.CreditStatusResponse;
import com.PetFit.backend.subscription.presentation.dto.response.SubscriptionResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionUseCase {

    /** 프로토타입: 결제 mock이므로 1개월(30일) 고정 */
    private static final int PREMIUM_DAYS = 30;

    private final SubscriptionService subscriptionService;
    private final CreditService creditService;

    @Transactional(readOnly = true)
    public SubscriptionResponse getMy(String userId) {
        Subscription s = subscriptionService.getOrCreateActive(userId);
        return SubscriptionResponse.from(s);
    }

    public SubscriptionResponse upgrade(String userId) {
        Subscription s = subscriptionService.upgradeToPremium(userId, PREMIUM_DAYS);
        return SubscriptionResponse.from(s);
    }

    public SubscriptionResponse cancel(String userId) {
        Subscription s = subscriptionService.cancel(userId);
        return SubscriptionResponse.from(s);
    }

    @Transactional(readOnly = true)
    public CreditStatusResponse getCredits(String userId) {
        return CreditStatusResponse.from(creditService.getStatus(userId));
    }
}
