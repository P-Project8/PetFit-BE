package com.PetFit.backend.review.domain.repository;

import com.PetFit.backend.review.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByProduct_IdAndDeletedAtIsNull(Long productId, Pageable pageable);

    Optional<Review> findByIdAndUserIdAndDeletedAtIsNull(Long id, String userId);

    boolean existsByUserIdAndOrder_IdAndProduct_IdAndDeletedAtIsNull(String userId, Long orderId, Long productId);

    long countByUserIdAndDeletedAtIsNull(String userId);

    long countByProduct_IdAndDeletedAtIsNull(Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.product.id = :productId AND r.deletedAt IS NULL")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT r.product.id, COALESCE(AVG(r.rating), 0), COUNT(r) FROM Review r WHERE r.deletedAt IS NULL GROUP BY r.product.id")
    java.util.List<Object[]> findReviewStatsGroupByProductId();
}
