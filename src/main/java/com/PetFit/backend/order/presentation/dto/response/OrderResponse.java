package com.PetFit.backend.order.presentation.dto.response;

import com.PetFit.backend.order.domain.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Integer totalPrice,
        String status,
        String address,
        String phone,
        String recipientName,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getAddress(),
                order.getPhone(),
                order.getRecipientName(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
