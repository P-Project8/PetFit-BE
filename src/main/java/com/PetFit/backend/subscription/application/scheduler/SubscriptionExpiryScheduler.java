package com.PetFit.backend.subscription.application.scheduler;

import com.PetFit.backend.notification.domain.entity.Notification;
import com.PetFit.backend.notification.domain.service.NotificationService;
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
    private final NotificationService notificationService;

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

            notificationService.publish(
                    s.getUserId(),
                    Notification.TYPE_SUBSCRIPTION_EXPIRED,
                    "프리미엄 구독 만료",
                    "프리미엄 구독이 만료되어 FREE 플랜으로 전환되었습니다. 다시 업그레이드하시려면 구독 페이지를 이용해 주세요.",
                    "SUBSCRIPTION",
                    s.getId());

            log.info("[SubscriptionExpiryScheduler] expired premium userId={} subscriptionId={}",
                    s.getUserId(), s.getId());
        }
        log.info("[SubscriptionExpiryScheduler] processed {} expired premium subscriptions", expired.size());
    }
}
