package com.PetFit.backend.pet.presentation.dto.response;

import com.PetFit.backend.product.domain.entity.Product;

import java.util.List;

public record SimilarPetCurationResponse(
        Long petId,
        String petName,
        Double chestSize,
        Integer similarUserCount,
        List<RecommendedProduct> products
) {
    public record RecommendedProduct(
            Long productId,
            String name,
            Integer price,
            String thumbnailUrl,
            Long orderCount,
            Integer popularityPercent
    ) {
        public static RecommendedProduct of(Product product, Long orderCount, int totalOrders) {
            int percent = totalOrders > 0 ? (int) Math.round(orderCount * 100.0 / totalOrders) : 0;
            return new RecommendedProduct(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getThumbnailUrl(),
                    orderCount,
                    percent
            );
        }
    }
}
