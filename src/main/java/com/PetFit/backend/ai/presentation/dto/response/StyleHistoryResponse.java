package com.PetFit.backend.ai.presentation.dto.response;

import com.PetFit.backend.ai.domain.entity.AiStyling;

import java.time.LocalDateTime;

public record StyleHistoryResponse(
        Long id,
        String petImageUrl,
        String clothImageUrl,
        Long productId,
        String resultImageUrl,
        String status,
        LocalDateTime createdAt
) {
    public static StyleHistoryResponse from(AiStyling entity) {
        return new StyleHistoryResponse(
                entity.getId(),
                entity.getPetImageUrl(),
                entity.getClothImageUrl(),
                entity.getProductId(),
                entity.getResultImageUrl(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
