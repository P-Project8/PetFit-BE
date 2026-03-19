package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.wishlist.presentation.dto.request.AddWishlistRequest;
import com.PetFit.backend.wishlist.presentation.dto.response.WishlistResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "찜 목록", description = "찜하기 API")
public interface WishlistApi extends BaseApi {

    @Operation(summary = "찜 추가", description = "상품을 찜 목록에 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "이미 찜한 상품"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    BaseResponse<WishlistResponse> addWishlist(String userId, AddWishlistRequest request);

    @Operation(summary = "찜 목록 조회", description = "내 찜 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<List<WishlistResponse>> getWishlist(String userId);

    @Operation(summary = "찜 해제", description = "찜 목록에서 상품을 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "찜 항목을 찾을 수 없음")
    })
    BaseResponse<Void> removeWishlist(String userId, @Parameter(description = "상품 ID") Long productId);
}
