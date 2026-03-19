package com.PetFit.backend.order.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.entity.ProductOption;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;
}
