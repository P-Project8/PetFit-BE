package com.PetFit.backend.subscription.presentation.dto.response;

import com.PetFit.backend.subscription.domain.service.CreditService;

public record CreditStatusResponse(
        String plan,
        long used,
        long monthlyLimit,
        long remaining,
        boolean unlimited
) {
    public static CreditStatusResponse from(CreditService.CreditStatus status) {
        return new CreditStatusResponse(
                status.plan(),
                status.used(),
                status.unlimited() ? -1 : status.monthlyLimit(),
                status.unlimited() ? -1 : status.remaining(),
                status.unlimited()
        );
    }
}
