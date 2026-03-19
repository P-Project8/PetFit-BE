package com.PetFit.backend.product.domain.repository;

import com.PetFit.backend.product.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
