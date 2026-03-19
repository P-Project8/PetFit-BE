package com.PetFit.backend.auth.domain.email.presentation.dto.response;

public record EmailVerificationResponse(
        boolean verified,
        long expiresInSec
) {}
