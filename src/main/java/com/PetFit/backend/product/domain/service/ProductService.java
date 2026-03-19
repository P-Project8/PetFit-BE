package com.PetFit.backend.product.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.ProductErrorStatus;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.entity.ProductOption;
import com.PetFit.backend.product.domain.repository.ProductOptionRepository;
import com.PetFit.backend.product.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public Product findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ProductErrorStatus.PRODUCT_NOT_FOUND));
        if (product.isDeleted()) {
            throw new RestApiException(ProductErrorStatus.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    public ProductOption findOptionById(Long id) {
        return productOptionRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ProductErrorStatus.PRODUCT_OPTION_NOT_FOUND));
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAllByDeletedAtIsNull(pageable);
    }

    public Page<Product> search(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(keyword, pageable);
    }

    public Page<Product> filter(Long categoryId, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.findByFilter(categoryId, minPrice, maxPrice, pageable);
    }

    public Page<Product> findAllSortedByReviewCount(Pageable pageable) {
        return productRepository.findAllSortedByReviewCount(pageable);
    }
}
