package com.PetFit.backend.cart.presentation.dto.response;

import com.PetFit.backend.cart.domain.entity.Cart;

public record CartResponse(
        Long id,
        Long productId,
        String productName,
        String thumbnailUrl,
        Long productOptionId,
        String size,
        String color,
        Integer price,
        Integer additionalPrice,
        Integer quantity
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getProduct().getId(),
                cart.getProduct().getName(),
                cart.getProduct().getThumbnailUrl(),
                cart.getProductOption() != null ? cart.getProductOption().getId() : null,
                cart.getProductOption() != null ? cart.getProductOption().getSize() : null,
                cart.getProductOption() != null ? cart.getProductOption().getColor() : null,
                cart.getProduct().getPrice(),
                cart.getProductOption() != null ? cart.getProductOption().getAdditionalPrice() : 0,
                cart.getQuantity()
        );
    }
}
