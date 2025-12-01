package com.PetFit.backend.auth.domain.auth.application.dto.response;

public record TokenReissueResponse(
        String accessToken,
        String refreshToken
) {}