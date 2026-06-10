package com.PetFit.backend.notification.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 알림.
 * 폴링 기반 — 클라이언트가 주기적으로 unread-count 또는 목록 조회.
 *
 * 알림 타입:
 * - GALLERY_LIKED: 내 갤러리에 좋아요 (targetId = galleryId)
 * - GALLERY_COMMENTED: 내 갤러리에 댓글 (targetId = galleryId)
 * - CREDIT_WARNING: AI 크레딧 임계점 도달 (FREE 2/3 사용)
 * - CREDIT_EXHAUSTED: 크레딧 모두 사용 (4번째 시도 차단)
 * - SUBSCRIPTION_EXPIRED: PREMIUM 구독 만료
 */
@Entity
@Getter
@Table(name = "notifications",
        indexes = {
                @Index(name = "idx_noti_user_read",
                        columnList = "userId,isRead,createdAt")
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    public static final String TYPE_GALLERY_LIKED = "GALLERY_LIKED";
    public static final String TYPE_GALLERY_COMMENTED = "GALLERY_COMMENTED";
    public static final String TYPE_CREDIT_WARNING = "CREDIT_WARNING";
    public static final String TYPE_CREDIT_EXHAUSTED = "CREDIT_EXHAUSTED";
    public static final String TYPE_SUBSCRIPTION_EXPIRED = "SUBSCRIPTION_EXPIRED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 알림 수신자 */
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    /** 클라이언트가 이동할 대상 — Gallery면 galleryId, 등 */
    @Column
    private Long targetId;

    @Column(length = 30)
    private String targetType;     // "GALLERY", "SUBSCRIPTION" 등

    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    public void markRead() {
        this.isRead = true;
    }

    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }
}
