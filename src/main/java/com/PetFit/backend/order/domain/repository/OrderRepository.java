package com.PetFit.backend.order.domain.repository;

import com.PetFit.backend.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);

    List<Order> findTop5ByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(String userId);

    Optional<Order> findByIdAndUserIdAndDeletedAtIsNull(Long id, String userId);

    long countByUserIdAndDeletedAtIsNull(String userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deletedAt IS NULL")
    long countAll();

    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.deletedAt IS NULL AND o.createdAt >= :since")
    long countSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.deletedAt IS NULL")
    long sumTotalRevenue();
}
