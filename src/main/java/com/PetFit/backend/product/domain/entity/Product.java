package com.PetFit.backend.product.domain.entity;

import com.PetFit.backend.category.domain.entity.Category;
import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String thumbnailUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isNew = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isHot = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isSale = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer discountRate = 0;

    private String productUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    public void decreaseStock(int quantity) {
        this.stockQuantity -= quantity;
    }
}
