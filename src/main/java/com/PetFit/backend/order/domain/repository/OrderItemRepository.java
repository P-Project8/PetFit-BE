package com.PetFit.backend.order.domain.repository;

import com.PetFit.backend.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
            "FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.product.id = :productId " +
            "AND oi.order.deletedAt IS NULL")
    boolean existsByOrderUserIdAndProductId(@Param("userId") String userId, @Param("productId") Long productId);

    /**
     * 특정 사용자 그룹이 가장 많이 주문한 상품 ID와 주문 횟수를 집계.
     * row[0] = productId(Long), row[1] = orderCount(Long)
     */
    @Query("SELECT oi.product.id, COUNT(oi) as cnt FROM OrderItem oi " +
            "WHERE oi.order.userId IN :userIds AND oi.order.deletedAt IS NULL " +
            "GROUP BY oi.product.id ORDER BY cnt DESC")
    List<Object[]> findTopOrderedProductsByUserIds(@Param("userIds") Collection<String> userIds);

    /**
     * 특정 사용자 그룹의 사이즈 선택 분포 (전체 상품).
     * row[0] = size(String), row[1] = count(Long)
     */
    @Query("SELECT oi.productOption.size, COUNT(oi) as cnt FROM OrderItem oi " +
            "WHERE oi.order.userId IN :userIds AND oi.order.deletedAt IS NULL " +
            "AND oi.productOption.size IS NOT NULL AND oi.productOption.size <> '' " +
            "GROUP BY oi.productOption.size ORDER BY cnt DESC")
    List<Object[]> findSizeDistributionByUserIds(@Param("userIds") Collection<String> userIds);

    /**
     * 특정 사용자 그룹이 특정 상품에서 선택한 사이즈 분포.
     * row[0] = size(String), row[1] = count(Long)
     */
    @Query("SELECT oi.productOption.size, COUNT(oi) as cnt FROM OrderItem oi " +
            "WHERE oi.order.userId IN :userIds AND oi.product.id = :productId " +
            "AND oi.order.deletedAt IS NULL " +
            "AND oi.productOption.size IS NOT NULL AND oi.productOption.size <> '' " +
            "GROUP BY oi.productOption.size ORDER BY cnt DESC")
    List<Object[]> findSizeDistributionByUserIdsAndProduct(
            @Param("userIds") Collection<String> userIds,
            @Param("productId") Long productId);
}
