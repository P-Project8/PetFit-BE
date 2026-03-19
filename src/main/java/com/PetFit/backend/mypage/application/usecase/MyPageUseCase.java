package com.PetFit.backend.mypage.application.usecase;

import com.PetFit.backend.auth.domain.auth.domain.entity.User;
import com.PetFit.backend.auth.domain.auth.domain.service.UserService;
import com.PetFit.backend.mypage.presentation.dto.response.MyPageResponse;
import com.PetFit.backend.order.presentation.dto.response.OrderResponse;
import com.PetFit.backend.order.domain.service.OrderService;
import com.PetFit.backend.review.domain.service.ReviewService;
import com.PetFit.backend.wishlist.domain.service.WishlistService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageUseCase {

    private final UserService userService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final WishlistService wishlistService;

    public MyPageResponse getMyPage(String userId) {
        User user = userService.findByUserId(userId);

        long orderCount = orderService.countByUserId(userId);
        long reviewCount = reviewService.countByUserId(userId);
        long wishlistCount = wishlistService.countByUserId(userId);

        List<OrderResponse> recentOrders = orderService.findRecentByUserId(userId).stream()
                .map(OrderResponse::from)
                .toList();

        return MyPageResponse.create(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                orderCount,
                reviewCount,
                wishlistCount,
                recentOrders
        );
    }
}
