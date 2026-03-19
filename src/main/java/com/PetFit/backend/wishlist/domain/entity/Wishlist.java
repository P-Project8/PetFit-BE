package com.PetFit.backend.wishlist.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;
import com.PetFit.backend.product.domain.entity.Product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "wishlists", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wishlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
