package com.PetFit.backend.wishlist.domain.repository;

import com.PetFit.backend.wishlist.domain.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findAllByUserIdAndDeletedAtIsNull(String userId);

    Optional<Wishlist> findByUserIdAndProductIdAndDeletedAtIsNull(String userId, Long productId);

    boolean existsByUserIdAndProductIdAndDeletedAtIsNull(String userId, Long productId);

    java.util.Optional<Wishlist> findByUserIdAndProduct_Id(String userId, Long productId);

    long countByUserIdAndDeletedAtIsNull(String userId);

    long countByProduct_IdAndDeletedAtIsNull(Long productId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT w.product.id, COUNT(w) FROM Wishlist w WHERE w.deletedAt IS NULL GROUP BY w.product.id")
    java.util.List<Object[]> countByProductGrouped();
}
