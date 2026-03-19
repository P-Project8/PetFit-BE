package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.product.presentation.dto.response.ProductDetailResponse;
import com.PetFit.backend.product.presentation.dto.response.ProductListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "상품", description = "상품 조회 API")
public interface ProductApi extends BaseApi {

    @Operation(summary = "상품 목록 조회", description = "전체 상품을 페이징으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<Page<ProductListResponse>> getProducts(Pageable pageable);

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    BaseResponse<ProductDetailResponse> getProduct(@Parameter(description = "상품 ID") Long id);

    @Operation(summary = "상품 검색", description = "키워드로 상품을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    BaseResponse<Page<ProductListResponse>> searchProducts(
            @Parameter(description = "검색 키워드") String keyword, Pageable pageable);

    @Operation(summary = "상품 필터링", description = "카테고리, 가격 범위로 상품을 필터링합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터링 성공")
    })
    BaseResponse<Page<ProductListResponse>> filterProducts(
            @Parameter(description = "카테고리 ID") Long categoryId,
            @Parameter(description = "최소 가격") Integer minPrice,
            @Parameter(description = "최대 가격") Integer maxPrice,
            Pageable pageable);

    @Operation(summary = "큐레이션 상품 조회", description = "추천/큐레이션 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<Page<ProductListResponse>> getCuratedProducts();

    @Operation(summary = "인기 상품 조회", description = "리뷰 수 기준으로 인기 상품을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    BaseResponse<Page<ProductListResponse>> getPopularProducts(Pageable pageable);
}
