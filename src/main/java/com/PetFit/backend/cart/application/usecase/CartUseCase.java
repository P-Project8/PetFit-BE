package com.PetFit.backend.cart.application.usecase;

import com.PetFit.backend.cart.presentation.dto.request.AddCartRequest;
import com.PetFit.backend.cart.presentation.dto.request.UpdateCartQuantityRequest;
import com.PetFit.backend.cart.presentation.dto.response.CartResponse;
import com.PetFit.backend.cart.domain.entity.Cart;
import com.PetFit.backend.cart.domain.service.CartService;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.entity.ProductOption;
import com.PetFit.backend.product.domain.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartUseCase {

    private final CartService cartService;
    private final ProductService productService;

    public CartResponse addCart(String userId, AddCartRequest request) {
        Product product = productService.findById(request.productId());
        ProductOption productOption = request.productOptionId() != null
                ? productService.findOptionById(request.productOptionId())
                : null;

        Optional<Cart> existing = cartService.findDuplicate(
                userId, request.productId(), request.productOptionId());

        if (existing.isPresent()) {
            Cart cart = existing.get();
            cart.addQuantity(request.quantity());
            return CartResponse.from(cart);
        }

        Cart cart = Cart.builder()
                .userId(userId)
                .product(product)
                .productOption(productOption)
                .quantity(request.quantity())
                .build();

        return CartResponse.from(cartService.save(cart));
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCart(String userId) {
        return cartService.findAllByUserId(userId).stream()
                .map(CartResponse::from)
                .toList();
    }

    public CartResponse updateQuantity(String userId, Long cartId, UpdateCartQuantityRequest request) {
        Cart cart = cartService.findByIdAndUserId(cartId, userId);
        cart.updateQuantity(request.quantity());
        return CartResponse.from(cart);
    }

    public void deleteCartItem(String userId, Long cartId) {
        Cart cart = cartService.findByIdAndUserId(cartId, userId);
        cartService.softDelete(cart);
    }
}
