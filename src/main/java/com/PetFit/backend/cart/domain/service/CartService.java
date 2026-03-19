package com.PetFit.backend.cart.domain.service;

import com.PetFit.backend.cart.domain.entity.Cart;
import com.PetFit.backend.cart.domain.repository.CartRepository;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.CartErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart findByIdAndUserId(Long id, String userId) {
        return cartRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new RestApiException(CartErrorStatus.CART_ITEM_NOT_FOUND));
    }

    public List<Cart> findAllByUserId(String userId) {
        return cartRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    public Optional<Cart> findDuplicate(String userId, Long productId, Long productOptionId) {
        if (productOptionId != null) {
            return cartRepository.findByUserIdAndProductIdAndProductOptionIdAndDeletedAtIsNull(
                    userId, productId, productOptionId);
        }
        return cartRepository.findByUserIdAndProductIdAndProductOptionIsNullAndDeletedAtIsNull(
                userId, productId);
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    public void softDelete(Cart cart) {
        cart.deleted();
        cartRepository.save(cart);
    }

    public void softDeleteAll(List<Cart> carts) {
        carts.forEach(Cart::deleted);
        cartRepository.saveAll(carts);
    }
}
