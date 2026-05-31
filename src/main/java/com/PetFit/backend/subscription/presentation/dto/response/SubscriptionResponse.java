package com.PetFit.backend.subscription.presentation.dto.response;

import com.PetFit.backend.subscription.domain.entity.Subscription;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        String plan,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isPremium
) {
    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(
                s.getId(),
                s.getPlan(),
                s.getStatus(),
                s.getStartDate(),
                s.getEndDate(),
                s.isPremium()
        );
    }
}
