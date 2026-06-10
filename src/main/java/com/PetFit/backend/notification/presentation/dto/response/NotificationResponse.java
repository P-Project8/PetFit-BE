package com.PetFit.backend.notification.presentation.dto.response;

import com.PetFit.backend.notification.domain.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String title,
        String message,
        String targetType,
        Long targetId,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getTargetType(),
                n.getTargetId(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
