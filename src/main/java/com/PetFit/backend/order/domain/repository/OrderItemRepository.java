package com.PetFit.backend.order.domain.repository;

import com.PetFit.backend.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
            "FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.product.id = :productId " +
            "AND oi.order.deletedAt IS NULL")
    boolean existsByOrderUserIdAndProductId(@Param("userId") String userId, @Param("productId") Long productId);
}
