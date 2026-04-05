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

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductUseCase {

    private final ProductService productService;
    private final ReviewService reviewService;

    public Page<ProductListResponse> getProducts(Pageable pageable) {
        Map<Long, double[]> statsMap = reviewService.getReviewStatsMap();
        return productService.findAll(pageable).map(product -> {
            double[] stats = statsMap.getOrDefault(product.getId(), new double[]{0.0, 0.0});
            return ProductListResponse.from(product, stats[0], (long) stats[1]);
        });
    }

    public ProductDetailResponse getProduct(Long id) {
        Product product = productService.findById(id);
        Double avgRating = reviewService.getAverageRatingByProductId(id);
        Long reviewCount = reviewService.countByProductId(id);
        return ProductDetailResponse.from(product, avgRating, reviewCount);
    }

    public Page<ProductListResponse> searchProducts(String keyword, Pageable pageable) {
        Map<Long, double[]> statsMap = reviewService.getReviewStatsMap();
        return productService.search(keyword, pageable).map(product -> {
            double[] stats = statsMap.getOrDefault(product.getId(), new double[]{0.0, 0.0});
            return ProductListResponse.from(product, stats[0], (long) stats[1]);
        });
    }

    public Page<ProductListResponse> filterProducts(Long categoryId, Integer minPrice, Integer maxPrice, Pageable pageable) {
        Map<Long, double[]> statsMap = reviewService.getReviewStatsMap();
        return productService.filter(categoryId, minPrice, maxPrice, pageable).map(product -> {
            double[] stats = statsMap.getOrDefault(product.getId(), new double[]{0.0, 0.0});
            return ProductListResponse.from(product, stats[0], (long) stats[1]);
        });
    }

    public Page<ProductListResponse> getCuratedProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Map<Long, double[]> statsMap = reviewService.getReviewStatsMap();
        return productService.findAll(pageable).map(product -> {
            double[] stats = statsMap.getOrDefault(product.getId(), new double[]{0.0, 0.0});
            return ProductListResponse.from(product, stats[0], (long) stats[1]);
        });
    }

    public Page<ProductListResponse> getProductsSortedByPopularity(Pageable pageable) {
        Map<Long, double[]> statsMap = reviewService.getReviewStatsMap();
        return productService.findAllSortedByReviewCount(pageable).map(product -> {
            double[] stats = statsMap.getOrDefault(product.getId(), new double[]{0.0, 0.0});
            return ProductListResponse.from(product, stats[0], (long) stats[1]);
        });
    }
}
