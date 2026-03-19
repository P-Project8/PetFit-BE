package com.PetFit.backend.product.presentation.dto.response;

import com.PetFit.backend.product.domain.entity.ProductImage;

public record ProductImageResponse(
        Long id,
        String imageUrl,
        Integer imageOrder
) {
    public static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getImageOrder()
        );
    }
}
