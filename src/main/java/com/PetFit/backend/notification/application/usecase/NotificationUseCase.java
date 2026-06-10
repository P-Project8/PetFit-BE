package com.PetFit.backend.notification.application.usecase;

import com.PetFit.backend.notification.domain.service.NotificationService;
import com.PetFit.backend.notification.presentation.dto.response.NotificationResponse;
import com.PetFit.backend.notification.presentation.dto.response.UnreadCountResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationUseCase {

    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findMy(String userId, Pageable pageable) {
        return notificationService.findMy(userId, pageable).map(NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(String userId) {
        return new UnreadCountResponse(notificationService.countUnread(userId));
    }

    public NotificationResponse markRead(String userId, Long notificationId) {
        return NotificationResponse.from(
                notificationService.markRead(notificationId, userId));
    }

    public int markAllRead(String userId) {
        return notificationService.markAllRead(userId);
    }
}
