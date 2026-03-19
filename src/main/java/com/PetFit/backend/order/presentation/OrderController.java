package com.PetFit.backend.order.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.OrderApi;
import com.PetFit.backend.order.presentation.dto.request.CreateOrderRequest;
import com.PetFit.backend.order.presentation.dto.response.OrderResponse;
import com.PetFit.backend.order.application.usecase.OrderUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController implements OrderApi {

    private final OrderUseCase orderUseCase;

    @PostMapping
    @Override
    public BaseResponse<OrderResponse> createOrder(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody CreateOrderRequest request) {
        return BaseResponse.onSuccess(orderUseCase.createOrder(userId, request));
    }

    @GetMapping
    @Override
    public BaseResponse<Page<OrderResponse>> getOrders(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return BaseResponse.onSuccess(orderUseCase.getOrders(userId, pageable));
    }

    @GetMapping("/{id}")
    @Override
    public BaseResponse<OrderResponse> getOrder(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long id) {
        return BaseResponse.onSuccess(orderUseCase.getOrder(userId, id));
    }

    @PatchMapping("/{id}/cancel")
    @Override
    public BaseResponse<OrderResponse> cancelOrder(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long id) {
        return BaseResponse.onSuccess(orderUseCase.cancelOrder(userId, id));
    }
}
