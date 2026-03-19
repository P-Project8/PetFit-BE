package com.PetFit.backend.product.presentation.dto.response;

import com.PetFit.backend.product.domain.entity.Product;

import java.time.LocalDateTime;

public record ProductListResponse(
        Long id,
        String name,
        Integer price,
        String thumbnailUrl,
        String categoryName,
        Boolean isNew,
        Boolean isHot,
        Boolean isSale,
        Integer discountRate,
        String productUrl,
        LocalDateTime createdAt
) {
    public static ProductListResponse from(Product product) {
        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnailUrl(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getIsNew(),
                product.getIsHot(),
                product.getIsSale(),
                product.getDiscountRate(),
                product.getProductUrl(),
                product.getCreatedAt()
        );
    }
}
