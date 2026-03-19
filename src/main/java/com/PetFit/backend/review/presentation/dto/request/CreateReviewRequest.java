package com.PetFit.backend.review.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        Integer rating,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String content,

        String imageUrl
) {
}
