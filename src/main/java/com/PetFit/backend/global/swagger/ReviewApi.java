package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.review.presentation.dto.request.CreateReviewRequest;
import com.PetFit.backend.review.presentation.dto.request.UpdateReviewRequest;
import com.PetFit.backend.review.presentation.dto.response.ReviewResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "리뷰", description = "상품 리뷰 API")
public interface ReviewApi extends BaseApi {

    @Operation(summary = "리뷰 작성", description = "주문한 상품에 대한 리뷰를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "주문 이력 없음 또는 이미 리뷰 작성됨")
    })
    BaseResponse<ReviewResponse> createReview(String userId, CreateReviewRequest request);

    @Operation(summary = "상품 리뷰 조회", description = "상품별 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<Page<ReviewResponse>> getProductReviews(
            @Parameter(description = "상품 ID") Long productId, Pageable pageable);

    @Operation(summary = "리뷰 수정", description = "내가 작성한 리뷰를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    BaseResponse<ReviewResponse> updateReview(String userId, @Parameter(description = "리뷰 ID") Long id, UpdateReviewRequest request);

    @Operation(summary = "리뷰 삭제", description = "내가 작성한 리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    BaseResponse<Void> deleteReview(String userId, @Parameter(description = "리뷰 ID") Long id);
}
