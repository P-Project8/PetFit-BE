package com.PetFit.backend.mypage.presentation.dto.response;

import com.PetFit.backend.order.presentation.dto.response.OrderResponse;

import java.util.List;

public record MyPageResponse(
        String userId,
        String name,
        String email,
        long orderCount,
        long reviewCount,
        long wishlistCount,
        List<OrderResponse> recentOrders
) {
    public static MyPageResponse create(
            String userId, String name, String email,
            long orderCount, long reviewCount, long wishlistCount,
            List<OrderResponse> recentOrders) {
        return new MyPageResponse(userId, name, email, orderCount, reviewCount, wishlistCount, recentOrders);
    }
}
