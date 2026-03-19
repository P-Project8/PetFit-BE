package com.PetFit.backend.wishlist.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddWishlistRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId
) {
}
