package com.PetFit.backend.auth.domain.auth.presentation.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}