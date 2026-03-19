package com.PetFit.backend.wishlist.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.WishlistApi;
import com.PetFit.backend.wishlist.presentation.dto.request.AddWishlistRequest;
import com.PetFit.backend.wishlist.presentation.dto.response.WishlistResponse;
import com.PetFit.backend.wishlist.application.usecase.WishlistUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController implements WishlistApi {

    private final WishlistUseCase wishlistUseCase;

    @PostMapping
    @Override
    public BaseResponse<WishlistResponse> addWishlist(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody AddWishlistRequest request) {
        return BaseResponse.onSuccess(wishlistUseCase.addWishlist(userId, request));
    }

    @GetMapping
    @Override
    public BaseResponse<List<WishlistResponse>> getWishlist(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(wishlistUseCase.getWishlist(userId));
    }

    @DeleteMapping("/{productId}")
    @Override
    public BaseResponse<Void> removeWishlist(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long productId) {
        wishlistUseCase.removeWishlist(userId, productId);
        return BaseResponse.onSuccess();
    }

    @GetMapping("/counts")
    public BaseResponse<Map<Long, Long>> getWishCounts() {
        return BaseResponse.onSuccess(wishlistUseCase.getWishCountMap());
    }
}
