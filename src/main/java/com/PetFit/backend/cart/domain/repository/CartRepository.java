package com.PetFit.backend.cart.domain.repository;

import com.PetFit.backend.cart.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUserIdAndDeletedAtIsNull(String userId);

    Optional<Cart> findByIdAndUserIdAndDeletedAtIsNull(Long id, String userId);

    Optional<Cart> findByUserIdAndProductIdAndProductOptionIdAndDeletedAtIsNull(
            String userId, Long productId, Long productOptionId);

    Optional<Cart> findByUserIdAndProductIdAndProductOptionIsNullAndDeletedAtIsNull(
            String userId, Long productId);
}
