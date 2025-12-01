package com.PetFit.backend.auth.domain.email.application.dto.response;

public record EmailVerificationResponse(
        boolean verified,
        long expiresInSec
) {}
