package com.PetFit.backend.auth.domain.auth.application.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}