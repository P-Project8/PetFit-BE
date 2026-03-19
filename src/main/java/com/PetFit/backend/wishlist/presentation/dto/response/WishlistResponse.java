package com.PetFit.backend.wishlist.presentation.dto.response;

import com.PetFit.backend.wishlist.domain.entity.Wishlist;

public record WishlistResponse(
        Long id,
        Long productId,
        String productName,
        Integer price,
        String thumbnailUrl
) {
    public static WishlistResponse from(Wishlist wishlist) {
        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getProduct().getId(),
                wishlist.getProduct().getName(),
                wishlist.getProduct().getPrice(),
                wishlist.getProduct().getThumbnailUrl()
        );
    }
}
