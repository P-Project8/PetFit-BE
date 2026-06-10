package com.PetFit.backend.admin.application.usecase;

import com.PetFit.backend.admin.domain.service.AdminGuard;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse.AiStats;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse.GalleryStats;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse.OrderStats;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse.SubscriptionStats;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse.UserStats;
import com.PetFit.backend.ai.domain.repository.AiStylingRepository;
import com.PetFit.backend.auth.domain.auth.domain.repository.UserRepository;
import com.PetFit.backend.gallery.domain.repository.GalleryRepository;
import com.PetFit.backend.order.domain.repository.OrderRepository;
import com.PetFit.backend.subscription.domain.entity.Subscription;
import com.PetFit.backend.subscription.domain.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 관리자 통계 대시보드.
 * 단일 호출로 모든 핵심 지표 반환 — 발표 시연/관리자 화면용.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsUseCase {

    private final AdminGuard adminGuard;
    private final UserRepository userRepository;
    private final AiStylingRepository aiStylingRepository;
    private final OrderRepository orderRepository;
    private final GalleryRepository galleryRepository;
    private final SubscriptionRepository subscriptionRepository;

    public AdminStatsResponse collect(String userId) {
        adminGuard.verify(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        LocalDateTime monthAgo = now.minusDays(30);

        return new AdminStatsResponse(
                buildUserStats(weekAgo, monthAgo),
                buildAiStats(weekAgo),
                buildOrderStats(weekAgo),
                buildGalleryStats(),
                buildSubscriptionStats(),
                now
        );
    }

    private UserStats buildUserStats(LocalDateTime weekAgo, LocalDateTime monthAgo) {
        return new UserStats(
                userRepository.countAll(),
                userRepository.countSince(weekAgo),
                userRepository.countSince(monthAgo)
        );
    }

    private AiStats buildAiStats(LocalDateTime weekAgo) {
        long total = aiStylingRepository.countAll();
        long completed = aiStylingRepository.countByStatus("COMPLETED");
        long failed = aiStylingRepository.countByStatus("FAILED");
        long pending = aiStylingRepository.countByStatus("PENDING");
        long thisWeek = aiStylingRepository.countSince(weekAgo);
        double successRate = total == 0 ? 0.0 :
                Math.round(100.0 * completed / total * 10) / 10.0;
        return new AiStats(total, completed, failed, pending, thisWeek, successRate);
    }

    private OrderStats buildOrderStats(LocalDateTime weekAgo) {
        return new OrderStats(
                orderRepository.countAll(),
                orderRepository.countSince(weekAgo),
                orderRepository.sumTotalRevenue()
        );
    }

    private GalleryStats buildGalleryStats() {
        return new GalleryStats(
                galleryRepository.countAll(),
                galleryRepository.sumLikes(),
                galleryRepository.sumComments()
        );
    }

    private SubscriptionStats buildSubscriptionStats() {
        long freeActive = subscriptionRepository.countActiveByPlan(Subscription.PLAN_FREE);
        long premiumActive = subscriptionRepository.countActiveByPlan(Subscription.PLAN_PREMIUM);
        long totalActive = freeActive + premiumActive;
        double premiumRate = totalActive == 0 ? 0.0 :
                Math.round(100.0 * premiumActive / totalActive * 10) / 10.0;
        return new SubscriptionStats(freeActive, premiumActive, premiumRate);
    }
}
