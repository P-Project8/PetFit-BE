package com.PetFit.backend.product.domain.repository;

import com.PetFit.backend.product.domain.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}
