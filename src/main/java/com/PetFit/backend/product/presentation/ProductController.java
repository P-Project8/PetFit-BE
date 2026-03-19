package com.PetFit.backend.product.presentation;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.ProductApi;
import com.PetFit.backend.product.presentation.dto.response.ProductDetailResponse;
import com.PetFit.backend.product.presentation.dto.response.ProductListResponse;
import com.PetFit.backend.product.application.usecase.ProductUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController implements ProductApi {

    private final ProductUseCase productUseCase;

    @GetMapping
    @Override
    public BaseResponse<Page<ProductListResponse>> getProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(productUseCase.getProducts(pageable));
    }

    @GetMapping("/{id}")
    @Override
    public BaseResponse<ProductDetailResponse> getProduct(@PathVariable Long id) {
        return BaseResponse.onSuccess(productUseCase.getProduct(id));
    }

    @GetMapping("/search")
    @Override
    public BaseResponse<Page<ProductListResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(productUseCase.searchProducts(keyword, pageable));
    }

    @GetMapping("/filter")
    @Override
    public BaseResponse<Page<ProductListResponse>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(productUseCase.filterProducts(categoryId, minPrice, maxPrice, pageable));
    }

    @GetMapping("/curated")
    @Override
    public BaseResponse<Page<ProductListResponse>> getCuratedProducts() {
        return BaseResponse.onSuccess(productUseCase.getCuratedProducts());
    }

    @GetMapping("/popular")
    @Override
    public BaseResponse<Page<ProductListResponse>> getPopularProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(productUseCase.getProductsSortedByPopularity(pageable));
    }
}
