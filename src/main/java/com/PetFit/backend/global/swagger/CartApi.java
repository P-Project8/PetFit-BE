package com.PetFit.backend.global.swagger;

import com.PetFit.backend.cart.presentation.dto.request.AddCartRequest;
import com.PetFit.backend.cart.presentation.dto.request.UpdateCartQuantityRequest;
import com.PetFit.backend.cart.presentation.dto.response.CartResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "장바구니", description = "장바구니 API")
public interface CartApi extends BaseApi {

    @Operation(summary = "장바구니 추가", description = "상품을 장바구니에 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    BaseResponse<CartResponse> addCart(String userId, AddCartRequest request);

    @Operation(summary = "장바구니 조회", description = "내 장바구니를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<List<CartResponse>> getCart(String userId);

    @Operation(summary = "장바구니 수량 변경", description = "장바구니 항목의 수량을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "404", description = "장바구니 항목을 찾을 수 없음")
    })
    BaseResponse<CartResponse> updateQuantity(String userId, @Parameter(description = "장바구니 ID") Long id, UpdateCartQuantityRequest request);

    @Operation(summary = "장바구니 삭제", description = "장바구니 항목을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "장바구니 항목을 찾을 수 없음")
    })
    BaseResponse<Void> deleteCartItem(String userId, @Parameter(description = "장바구니 ID") Long id);
}
