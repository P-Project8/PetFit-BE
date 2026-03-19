package com.PetFit.backend.product.application.usecase;

import com.PetFit.backend.product.presentation.dto.response.ProductDetailResponse;
import com.PetFit.backend.product.presentation.dto.response.ProductListResponse;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.service.ProductService;
import com.PetFit.backend.review.domain.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductUseCase {

    private final ProductService productService;
    private final ReviewService reviewService;

    public Page<ProductListResponse> getProducts(Pageable pageable) {
        return productService.findAll(pageable).map(ProductListResponse::from);
    }

    public ProductDetailResponse getProduct(Long id) {
        Product product = productService.findById(id);
        Double avgRating = reviewService.getAverageRatingByProductId(id);
        Long reviewCount = reviewService.countByProductId(id);
        return ProductDetailResponse.from(product, avgRating, reviewCount);
    }

    public Page<ProductListResponse> searchProducts(String keyword, Pageable pageable) {
        return productService.search(keyword, pageable).map(ProductListResponse::from);
    }

    public Page<ProductListResponse> filterProducts(Long categoryId, Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productService.filter(categoryId, minPrice, maxPrice, pageable).map(ProductListResponse::from);
    }

    public Page<ProductListResponse> getCuratedProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productService.findAll(pageable).map(ProductListResponse::from);
    }

    public Page<ProductListResponse> getProductsSortedByPopularity(Pageable pageable) {
        return productService.findAllSortedByReviewCount(pageable).map(ProductListResponse::from);
    }
}
