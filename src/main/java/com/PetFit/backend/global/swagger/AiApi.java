package com.PetFit.backend.global.swagger;

import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI Styling", description = "AI 가상 피팅 API")
public interface AiApi {

    @Operation(summary = "AI 스타일링", description = "반려동물 사진에 선택한 옷을 AI로 가상 피팅합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스타일링 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "500", description = "AI 서비스 오류")
    })
    BaseResponse<StyleResponse> generateStyling(StyleRequest request);
}
