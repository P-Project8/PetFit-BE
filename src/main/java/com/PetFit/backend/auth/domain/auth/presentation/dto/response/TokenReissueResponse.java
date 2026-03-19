package com.PetFit.backend.auth.domain.auth.presentation.dto.response;

public record TokenReissueResponse(
        String accessToken,
        String refreshToken
) {}