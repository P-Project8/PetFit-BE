package com.PetFit.backend.ai.presentation.dto.response;

public record StyleResponse(
        Long stylingId,
        String resultImageUrl,
        String resultImageBase64
) {}
