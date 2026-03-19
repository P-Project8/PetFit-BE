package com.PetFit.backend.review.presentation.dto.response;

import com.PetFit.backend.review.domain.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String userId,
        Long productId,
        Integer rating,
        String content,
        String imageUrl,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUserId(),
                review.getProduct().getId(),
                review.getRating(),
                review.getContent(),
                review.getImageUrl(),
                review.getCreatedAt()
        );
    }
}
