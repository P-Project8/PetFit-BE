package com.PetFit.backend.review.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;
import com.PetFit.backend.order.domain.entity.Order;
import com.PetFit.backend.product.domain.entity.Product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "reviews")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String imageUrl;

    public void update(Integer rating, String content, String imageUrl) {
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
