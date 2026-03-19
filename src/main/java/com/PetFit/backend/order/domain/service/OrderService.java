package com.PetFit.backend.order.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.OrderErrorStatus;
import com.PetFit.backend.order.domain.entity.Order;
import com.PetFit.backend.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order findByIdAndUserId(Long id, String userId) {
        return orderRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new RestApiException(OrderErrorStatus.ORDER_NOT_FOUND));
    }

    public Page<Order> findAllByUserId(String userId, Pageable pageable) {
        return orderRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
    }

    public List<Order> findRecentByUserId(String userId) {
        return orderRepository.findTop5ByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
    }

    public long countByUserId(String userId) {
        return orderRepository.countByUserIdAndDeletedAtIsNull(userId);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
