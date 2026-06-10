package com.PetFit.backend.global.swagger;

import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleDownloadResponse;
import com.PetFit.backend.ai.presentation.dto.response.StyleHistoryResponse;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "AI Styling", description = "AI 가상 피팅 API (인증 필수)")
public interface AiApi {

    @Operation(summary = "AI 스타일링",
            description = "반려동물 사진에 선택한 옷을 AI로 가상 피팅합니다. " +
                    "petProfileId를 함께 보내면 반려견 체형 데이터(견종/체중/가슴둘레 등)가 프롬프트에 자동 주입되어 정확도가 향상됩니다. " +
                    "결과는 S3에 저장되고 사용자 이력에 기록됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스타일링 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "500", description = "AI 서비스 오류")
    })
    BaseResponse<StyleResponse> generateStyling(
            String userId,
            StyleRequest request);

    @Operation(summary = "스타일링 이력 조회", description = "내가 생성한 AI 스타일링 이력 목록을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    BaseResponse<List<StyleHistoryResponse>> getStylingHistory(String userId);

    @Operation(summary = "스타일링 결과 다운로드 (구독 등급별 차별화)",
            description = """
                    저장된 스타일링 결과를 base64 이미지로 다운로드합니다.
                    - FREE 플랜: 512px 다운사이즈 + 'PetFit Free' 워터마크 합성
                    - PREMIUM 플랜: 원본 해상도 그대로 + 워터마크 없음
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "스타일링 미완료"),
            @ApiResponse(responseCode = "403", description = "본인 스타일링이 아님"),
            @ApiResponse(responseCode = "404", description = "스타일링 결과 없음")
    })
    BaseResponse<StyleDownloadResponse> downloadStyling(
            String userId,
            @Parameter(description = "스타일링 ID") Long stylingId);
}
