package com.PetFit.backend.order.domain.repository;

import com.PetFit.backend.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);

    List<Order> findTop5ByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(String userId);

    Optional<Order> findByIdAndUserIdAndDeletedAtIsNull(Long id, String userId);

    long countByUserIdAndDeletedAtIsNull(String userId);
}
