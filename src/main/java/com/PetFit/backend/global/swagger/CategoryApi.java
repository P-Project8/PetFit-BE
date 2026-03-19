package com.PetFit.backend.global.swagger;

import com.PetFit.backend.category.presentation.dto.response.CategoryResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "카테고리", description = "상품 카테고리 API")
public interface CategoryApi extends BaseApi {

    @Operation(summary = "카테고리 목록 조회", description = "전체 카테고리를 트리 구조로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))
            )
    })
    BaseResponse<List<CategoryResponse>> getCategories();
}
