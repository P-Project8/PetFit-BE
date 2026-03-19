package com.PetFit.backend.product.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product_options")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String size;

    private String color;

    @Builder.Default
    @Column(nullable = false)
    private Integer additionalPrice = 0;

    @Column(nullable = false)
    private Integer stockQuantity;

    public void decreaseStock(int quantity) {
        this.stockQuantity -= quantity;
    }
}
