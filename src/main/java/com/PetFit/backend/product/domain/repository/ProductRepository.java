package com.PetFit.backend.product.domain.repository;

import com.PetFit.backend.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String keyword, Pageable pageable);

    Page<Product> findByCategoryIdAndDeletedAtIsNull(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL" +
            " AND (:categoryId IS NULL OR p.category.id = :categoryId)" +
            " AND (:minPrice IS NULL OR p.price >= :minPrice)" +
            " AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByFilter(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p LEFT JOIN Review r ON r.product = p AND r.deletedAt IS NULL" +
            " WHERE p.deletedAt IS NULL" +
            " GROUP BY p" +
            " ORDER BY COUNT(r) DESC")
    Page<Product> findAllSortedByReviewCount(Pageable pageable);
}
