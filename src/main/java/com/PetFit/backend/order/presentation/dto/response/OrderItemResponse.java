package com.PetFit.backend.order.presentation.dto.response;

import com.PetFit.backend.order.domain.entity.OrderItem;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String thumbnailUrl,
        String size,
        String color,
        Integer quantity,
        Integer price
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getThumbnailUrl(),
                item.getProductOption() != null ? item.getProductOption().getSize() : null,
                item.getProductOption() != null ? item.getProductOption().getColor() : null,
                item.getQuantity(),
                item.getPrice()
        );
    }
}
