package com.PetFit.backend.notification.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.NotificationErrorStatus;
import com.PetFit.backend.notification.domain.entity.Notification;
import com.PetFit.backend.notification.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 발행 — 호출자 트랜잭션과 분리. 알림 저장 실패가 본 비즈니스 흐름을 막지 않게 try-catch.
     */
    public void publish(String userId, String type, String title, String message,
                        String targetType, Long targetId) {
        try {
            Notification noti = Notification.builder()
                    .userId(userId)
                    .type(type)
                    .title(title)
                    .message(message)
                    .targetType(targetType)
                    .targetId(targetId)
                    .isRead(false)
                    .build();
            notificationRepository.save(noti);
        } catch (Exception e) {
            // 알림 실패가 좋아요/댓글/AI 호출 자체를 막아선 안 됨
            log.warn("[NotificationService] publish failed userId={} type={} message={}",
                    userId, type, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<Notification> findMy(String userId, Pageable pageable) {
        return notificationRepository
                .findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnread(String userId) {
        return notificationRepository
                .countByUserIdAndIsReadFalseAndDeletedAtIsNull(userId);
    }

    public Notification markRead(Long notificationId, String userId) {
        Notification noti = notificationRepository.findById(notificationId)
                .filter(n -> !n.isDeleted())
                .orElseThrow(() -> new RestApiException(NotificationErrorStatus.NOTIFICATION_NOT_FOUND));
        if (!noti.isOwnedBy(userId)) {
            throw new RestApiException(NotificationErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }
        noti.markRead();
        return notificationRepository.save(noti);
    }

    public int markAllRead(String userId) {
        return notificationRepository.markAllReadByUserId(userId);
    }
}
