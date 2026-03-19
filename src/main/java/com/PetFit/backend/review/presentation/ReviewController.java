package com.PetFit.backend.review.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.ReviewApi;
import com.PetFit.backend.review.presentation.dto.request.CreateReviewRequest;
import com.PetFit.backend.review.presentation.dto.request.UpdateReviewRequest;
import com.PetFit.backend.review.presentation.dto.response.ReviewResponse;
import com.PetFit.backend.review.application.usecase.ReviewUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewApi {

    private final ReviewUseCase reviewUseCase;

    @PostMapping
    @Override
    public BaseResponse<ReviewResponse> createReview(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return BaseResponse.onSuccess(reviewUseCase.createReview(userId, request));
    }

    @GetMapping("/product/{productId}")
    @Override
    public BaseResponse<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return BaseResponse.onSuccess(reviewUseCase.getProductReviews(productId, pageable));
    }

    @PatchMapping("/{id}")
    @Override
    public BaseResponse<ReviewResponse> updateReview(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        return BaseResponse.onSuccess(reviewUseCase.updateReview(userId, id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public BaseResponse<Void> deleteReview(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long id) {
        reviewUseCase.deleteReview(userId, id);
        return BaseResponse.onSuccess();
    }
}
