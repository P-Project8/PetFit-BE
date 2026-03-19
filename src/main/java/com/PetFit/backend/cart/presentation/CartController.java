package com.PetFit.backend.cart.presentation;

import com.PetFit.backend.cart.presentation.dto.request.AddCartRequest;
import com.PetFit.backend.cart.presentation.dto.request.UpdateCartQuantityRequest;
import com.PetFit.backend.cart.presentation.dto.response.CartResponse;
import com.PetFit.backend.cart.application.usecase.CartUseCase;
import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.CartApi;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController implements CartApi {

    private final CartUseCase cartUseCase;

    @PostMapping
    @Override
    public BaseResponse<CartResponse> addCart(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody AddCartRequest request) {
        return BaseResponse.onSuccess(cartUseCase.addCart(userId, request));
    }

    @GetMapping
    @Override
    public BaseResponse<List<CartResponse>> getCart(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(cartUseCase.getCart(userId));
    }

    @PatchMapping("/{id}")
    @Override
    public BaseResponse<CartResponse> updateQuantity(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartQuantityRequest request) {
        return BaseResponse.onSuccess(cartUseCase.updateQuantity(userId, id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public BaseResponse<Void> deleteCartItem(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long id) {
        cartUseCase.deleteCartItem(userId, id);
        return BaseResponse.onSuccess();
    }
}
