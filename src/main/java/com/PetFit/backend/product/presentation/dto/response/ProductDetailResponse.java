package com.PetFit.backend.product.presentation.dto.response;

import com.PetFit.backend.product.domain.entity.Product;

import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        Integer price,
        Integer stockQuantity,
        String categoryName,
        String thumbnailUrl,
        Boolean isNew,
        Boolean isHot,
        Boolean isSale,
        Integer discountRate,
        String productUrl,
        Double avgRating,
        Long reviewCount,
        List<ProductOptionResponse> options,
        List<ProductImageResponse> images
) {
    public static ProductDetailResponse from(Product product, Double avgRating, Long reviewCount) {
        List<ProductOptionResponse> optionResponses = product.getOptions().stream()
                .filter(o -> !o.isDeleted())
                .map(ProductOptionResponse::from)
                .toList();

        List<ProductImageResponse> imageResponses = product.getImages().stream()
                .filter(i -> !i.isDeleted())
                .map(ProductImageResponse::from)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getThumbnailUrl(),
                product.getIsNew(),
                product.getIsHot(),
                product.getIsSale(),
                product.getDiscountRate(),
                product.getProductUrl(),
                Math.round(avgRating * 10) / 10.0,
                reviewCount,
                optionResponses,
                imageResponses
        );
    }
}
