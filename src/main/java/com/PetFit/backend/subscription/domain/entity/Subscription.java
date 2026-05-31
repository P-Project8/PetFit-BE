package com.PetFit.backend.subscription.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 구독 정보.
 * - FREE: 월 3회 AI 스타일링 제공 (endDate = null, 영구)
 * - PREMIUM: AI 스타일링 무제한 (endDate 기준 30일 단위 갱신, 프로토타입은 결제 mock)
 *
 * 사용자당 ACTIVE 구독은 한 건이 원칙 (조회 시 가장 최신 ACTIVE를 사용).
 */
@Entity
@Getter
@Table(name = "subscriptions",
        indexes = {
                @Index(name = "idx_subscription_user_status", columnList = "userId,status")
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends BaseEntity {

    public static final String PLAN_FREE = "FREE";
    public static final String PLAN_PREMIUM = "PREMIUM";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String plan = PLAN_FREE;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = STATUS_ACTIVE;

    @Column(nullable = false)
    private LocalDateTime startDate;

    /** FREE 플랜은 null (영구). PREMIUM은 갱신일자. */
    @Column
    private LocalDateTime endDate;

    public boolean isPremium() {
        return PLAN_PREMIUM.equals(plan) && STATUS_ACTIVE.equals(status)
                && (endDate == null || endDate.isAfter(LocalDateTime.now()));
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }

    public boolean isExpired(LocalDateTime now) {
        return endDate != null && endDate.isBefore(now);
    }

    public void expire() {
        this.status = STATUS_EXPIRED;
    }

    public void cancel() {
        this.status = STATUS_CANCELLED;
    }

    public void upgrade(LocalDateTime now, int days) {
        this.plan = PLAN_PREMIUM;
        this.status = STATUS_ACTIVE;
        this.startDate = now;
        this.endDate = now.plusDays(days);
    }

    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }
}
