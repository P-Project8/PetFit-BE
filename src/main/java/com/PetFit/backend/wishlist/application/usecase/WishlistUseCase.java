package com.PetFit.backend.wishlist.application.usecase;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.WishlistErrorStatus;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.service.ProductService;
import com.PetFit.backend.wishlist.presentation.dto.request.AddWishlistRequest;
import com.PetFit.backend.wishlist.presentation.dto.response.WishlistResponse;
import com.PetFit.backend.wishlist.domain.entity.Wishlist;
import com.PetFit.backend.wishlist.domain.service.WishlistService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistUseCase {

    private final WishlistService wishlistService;
    private final ProductService productService;

    public WishlistResponse addWishlist(String userId, AddWishlistRequest request) {
        Product product = productService.findById(request.productId());

        // soft delete 포함 기존 레코드 확인
        var existing = wishlistService.findByUserIdAndProductIdIncludeDeleted(userId, request.productId());

        if (existing.isPresent()) {
            Wishlist wishlist = existing.get();
            if (!wishlist.isDeleted()) {
                throw new RestApiException(WishlistErrorStatus.ALREADY_WISHLISTED);
            }
            // soft delete된 레코드 복원
            wishlist.restore();
            return WishlistResponse.from(wishlistService.save(wishlist));
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .product(product)
                .build();

        return WishlistResponse.from(wishlistService.save(wishlist));
    }

    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(String userId) {
        return wishlistService.findAllByUserId(userId).stream()
                .map(WishlistResponse::from)
                .toList();
    }

    public void removeWishlist(String userId, Long productId) {
        Wishlist wishlist = wishlistService.findByUserIdAndProductId(userId, productId);
        wishlistService.softDelete(wishlist);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getWishCountMap() {
        return wishlistService.getWishCountMap();
    }
}
