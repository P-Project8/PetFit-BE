package com.PetFit.backend.review.application.usecase;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.ReviewErrorStatus;
import com.PetFit.backend.order.domain.repository.OrderItemRepository;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.service.ProductService;
import com.PetFit.backend.order.domain.entity.Order;
import com.PetFit.backend.order.domain.service.OrderService;
import com.PetFit.backend.review.presentation.dto.request.CreateReviewRequest;
import com.PetFit.backend.review.presentation.dto.request.UpdateReviewRequest;
import com.PetFit.backend.review.presentation.dto.response.ReviewResponse;
import com.PetFit.backend.review.domain.entity.Review;
import com.PetFit.backend.review.domain.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewUseCase {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public ReviewResponse createReview(String userId, CreateReviewRequest request) {
        // 주문 이력 검증
        if (!orderItemRepository.existsByOrderUserIdAndProductId(userId, request.productId())) {
            throw new RestApiException(ReviewErrorStatus.REVIEW_NOT_ELIGIBLE);
        }

        // 중복 리뷰 검증
        if (reviewService.existsByUserIdAndOrderIdAndProductId(userId, request.orderId(), request.productId())) {
            throw new RestApiException(ReviewErrorStatus.REVIEW_ALREADY_EXISTS);
        }

        Product product = productService.findById(request.productId());
        Order order = orderService.findByIdAndUserId(request.orderId(), userId);

        Review review = Review.builder()
                .userId(userId)
                .product(product)
                .order(order)
                .rating(request.rating())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .build();

        return ReviewResponse.from(reviewService.save(review));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        return reviewService.findAllByProductId(productId, pageable).map(ReviewResponse::from);
    }

    public ReviewResponse updateReview(String userId, Long reviewId, UpdateReviewRequest request) {
        Review review = reviewService.findByIdAndUserId(reviewId, userId);
        review.update(request.rating(), request.content(), request.imageUrl());
        return ReviewResponse.from(review);
    }

    public void deleteReview(String userId, Long reviewId) {
        Review review = reviewService.findByIdAndUserId(reviewId, userId);
        reviewService.softDelete(review);
    }
}
