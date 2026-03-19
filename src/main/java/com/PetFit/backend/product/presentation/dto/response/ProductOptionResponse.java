package com.PetFit.backend.product.presentation.dto.response;

import com.PetFit.backend.product.domain.entity.ProductOption;

public record ProductOptionResponse(
        Long id,
        String size,
        String color,
        Integer additionalPrice,
        Integer stockQuantity
) {
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getSize(),
                option.getColor(),
                option.getAdditionalPrice(),
                option.getStockQuantity()
        );
    }
}
