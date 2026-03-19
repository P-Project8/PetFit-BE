package com.PetFit.backend.review.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.ReviewErrorStatus;
import com.PetFit.backend.review.domain.entity.Review;
import com.PetFit.backend.review.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review findByIdAndUserId(Long id, String userId) {
        return reviewRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new RestApiException(ReviewErrorStatus.REVIEW_NOT_FOUND));
    }

    public Page<Review> findAllByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findAllByProduct_IdAndDeletedAtIsNull(productId, pageable);
    }

    public boolean existsByUserIdAndOrderIdAndProductId(String userId, Long orderId, Long productId) {
        return reviewRepository.existsByUserIdAndOrder_IdAndProduct_IdAndDeletedAtIsNull(userId, orderId, productId);
    }

    public long countByUserId(String userId) {
        return reviewRepository.countByUserIdAndDeletedAtIsNull(userId);
    }

    public long countByProductId(Long productId) {
        return reviewRepository.countByProduct_IdAndDeletedAtIsNull(productId);
    }

    public Double getAverageRatingByProductId(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public void softDelete(Review review) {
        review.deleted();
        reviewRepository.save(review);
    }
}
