package com.PetFit.backend.wishlist.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.WishlistErrorStatus;
import com.PetFit.backend.wishlist.domain.entity.Wishlist;
import com.PetFit.backend.wishlist.domain.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public List<Wishlist> findAllByUserId(String userId) {
        return wishlistRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    public Wishlist findByUserIdAndProductId(String userId, Long productId) {
        return wishlistRepository.findByUserIdAndProductIdAndDeletedAtIsNull(userId, productId)
                .orElseThrow(() -> new RestApiException(WishlistErrorStatus.WISHLIST_NOT_FOUND));
    }

    public boolean existsByUserIdAndProductId(String userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductIdAndDeletedAtIsNull(userId, productId);
    }

    public java.util.Optional<Wishlist> findByUserIdAndProductIdIncludeDeleted(String userId, Long productId) {
        return wishlistRepository.findByUserIdAndProduct_Id(userId, productId);
    }

    public long countByUserId(String userId) {
        return wishlistRepository.countByUserIdAndDeletedAtIsNull(userId);
    }

    public long countByProductId(Long productId) {
        return wishlistRepository.countByProduct_IdAndDeletedAtIsNull(productId);
    }

    public java.util.Map<Long, Long> getWishCountMap() {
        java.util.Map<Long, Long> map = new java.util.HashMap<>();
        for (Object[] row : wishlistRepository.countByProductGrouped()) {
            map.put((Long) row[0], (Long) row[1]);
        }
        return map;
    }

    public Wishlist save(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    public void softDelete(Wishlist wishlist) {
        wishlist.deleted();
        wishlistRepository.save(wishlist);
    }
}
