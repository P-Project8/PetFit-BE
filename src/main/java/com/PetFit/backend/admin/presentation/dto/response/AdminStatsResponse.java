package com.PetFit.backend.admin.presentation.dto.response;

import java.time.LocalDateTime;

/**
 * 관리자 통계 대시보드 응답.
 * 발표 시연용 단일 호출 — 모든 핵심 지표를 한 번에 반환.
 */
public record AdminStatsResponse(
        UserStats users,
        AiStats ai,
        OrderStats orders,
        GalleryStats gallery,
        SubscriptionStats subscriptions,
        LocalDateTime collectedAt
) {
    public record UserStats(
            long total,           // 누적 가입자
            long newThisWeek,     // 최근 7일 신규 가입
            long newThisMonth     // 최근 30일 신규 가입
    ) {}

    public record AiStats(
            long total,           // 누적 호출 (PENDING+COMPLETED+FAILED)
            long completed,       // 성공
            long failed,          // 실패
            long pending,         // 진행 중
            long thisWeek,        // 최근 7일
            double successRate    // 성공률 (%)
    ) {}

    public record OrderStats(
            long totalOrders,         // 누적 주문
            long ordersThisWeek,      // 최근 7일 주문
            long totalRevenue         // 누적 매출(원)
    ) {}

    public record GalleryStats(
            long totalPosts,      // 누적 게시물
            long totalLikes,      // 누적 좋아요
            long totalComments    // 누적 댓글
    ) {}

    public record SubscriptionStats(
            long freeActive,      // FREE 활성
            long premiumActive,   // PREMIUM 활성
            double premiumRate    // PREMIUM 비율 (%)
    ) {}
}
