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
        Double avgRating,
        Long reviewCount,
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
                0.0,
                0L,
                product.getCreatedAt()
        );
    }

    public static ProductListResponse from(Product product, Double avgRating, Long reviewCount) {
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
                avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0,
                reviewCount != null ? reviewCount : 0L,
                product.getCreatedAt()
        );
    }
}
