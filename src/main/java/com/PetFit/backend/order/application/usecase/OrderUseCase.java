package com.PetFit.backend.order.application.usecase;

import com.PetFit.backend.cart.domain.entity.Cart;
import com.PetFit.backend.cart.domain.service.CartService;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.OrderErrorStatus;
import com.PetFit.backend.global.exception.code.status.ProductErrorStatus;
import com.PetFit.backend.order.presentation.dto.request.CreateOrderRequest;
import com.PetFit.backend.order.presentation.dto.response.OrderResponse;
import com.PetFit.backend.order.domain.entity.Order;
import com.PetFit.backend.order.domain.entity.OrderItem;
import com.PetFit.backend.order.domain.entity.OrderStatus;
import com.PetFit.backend.order.domain.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderUseCase {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderResponse createOrder(String userId, CreateOrderRequest request) {
        List<Cart> cartItems = cartService.findAllByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RestApiException(OrderErrorStatus.EMPTY_CART);
        }

        int totalPrice = 0;
        for (Cart cart : cartItems) {
            int itemPrice = cart.getProduct().getPrice();
            if (cart.getProductOption() != null) {
                itemPrice += cart.getProductOption().getAdditionalPrice();
            }
            totalPrice += itemPrice * cart.getQuantity();

            // 재고 확인
            if (cart.getProductOption() != null) {
                if (cart.getProductOption().getStockQuantity() < cart.getQuantity()) {
                    throw new RestApiException(ProductErrorStatus.PRODUCT_OUT_OF_STOCK);
                }
            } else {
                if (cart.getProduct().getStockQuantity() < cart.getQuantity()) {
                    throw new RestApiException(ProductErrorStatus.PRODUCT_OUT_OF_STOCK);
                }
            }
        }

        Order order = Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .address(request.address())
                .phone(request.phone())
                .recipientName(request.recipientName())
                .build();

        for (Cart cart : cartItems) {
            int itemPrice = cart.getProduct().getPrice();
            if (cart.getProductOption() != null) {
                itemPrice += cart.getProductOption().getAdditionalPrice();
                cart.getProductOption().decreaseStock(cart.getQuantity());
            } else {
                cart.getProduct().decreaseStock(cart.getQuantity());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cart.getProduct())
                    .productOption(cart.getProductOption())
                    .quantity(cart.getQuantity())
                    .price(itemPrice * cart.getQuantity())
                    .build();

            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderService.save(order);

        // 장바구니 비우기
        cartService.softDeleteAll(cartItems);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(String userId, Pageable pageable) {
        return orderService.findAllByUserId(userId, pageable).map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String userId, Long orderId) {
        return OrderResponse.from(orderService.findByIdAndUserId(orderId, userId));
    }

    public OrderResponse cancelOrder(String userId, Long orderId) {
        Order order = orderService.findByIdAndUserId(orderId, userId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RestApiException(OrderErrorStatus.ORDER_ALREADY_CANCELLED);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RestApiException(OrderErrorStatus.ORDER_CANNOT_CANCEL);
        }

        order.cancel();
        return OrderResponse.from(order);
    }
}
