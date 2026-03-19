package com.PetFit.backend.cart.domain.entity;

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
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    @Column(nullable = false)
    private Integer quantity;

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(Integer quantity) {
        this.quantity += quantity;
    }
}
