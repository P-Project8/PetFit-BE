package com.PetFit.backend.subscription.application.scheduler;

import com.PetFit.backend.subscription.domain.entity.Subscription;
import com.PetFit.backend.subscription.domain.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 매일 새벽 1시(KST) 만료된 PREMIUM 구독을 EXPIRED로 전환하고
 * 동일 사용자에게 FREE 플랜을 자동 부여한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    @Transactional
    public void expirePremiums() {
        List<Subscription> expired = subscriptionService.findExpiredPremiums();
        if (expired.isEmpty()) {
            log.debug("[SubscriptionExpiryScheduler] no expired premium subscriptions");
            return;
        }

        for (Subscription s : expired) {
            s.expire();
            subscriptionService.save(s);
            subscriptionService.createFree(s.getUserId());
            log.info("[SubscriptionExpiryScheduler] expired premium userId={} subscriptionId={}",
                    s.getUserId(), s.getId());
        }
        log.info("[SubscriptionExpiryScheduler] processed {} expired premium subscriptions", expired.size());
    }
}
