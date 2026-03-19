package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.order.presentation.dto.request.CreateOrderRequest;
import com.PetFit.backend.order.presentation.dto.response.OrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "주문", description = "주문 API")
public interface OrderApi extends BaseApi {

    @Operation(summary = "주문 생성", description = "장바구니 상품으로 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "장바구니가 비어있거나 재고 부족")
    })
    BaseResponse<OrderResponse> createOrder(String userId, CreateOrderRequest request);

    @Operation(summary = "주문 목록 조회", description = "내 주문 목록을 페이징으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<Page<OrderResponse>> getOrders(String userId, Pageable pageable);

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 주문 상세를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    BaseResponse<OrderResponse> getOrder(String userId, @Parameter(description = "주문 ID") Long id);

    @Operation(summary = "주문 취소", description = "PENDING 상태의 주문을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    BaseResponse<OrderResponse> cancelOrder(String userId, @Parameter(description = "주문 ID") Long id);
}
