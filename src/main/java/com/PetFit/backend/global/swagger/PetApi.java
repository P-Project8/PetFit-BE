package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.pet.presentation.dto.request.CreatePetRequest;
import com.PetFit.backend.pet.presentation.dto.request.UpdatePetRequest;
import com.PetFit.backend.pet.presentation.dto.response.PetResponse;
import com.PetFit.backend.pet.presentation.dto.response.SimilarPetCurationResponse;
import com.PetFit.backend.pet.presentation.dto.response.SizeRecommendationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Pet", description = "반려견 프로필 API (인증 필수)")
public interface PetApi {

    @Operation(summary = "반려견 등록",
            description = "견종/체형 정보를 포함하여 반려견 프로필을 등록합니다. 사용자당 최대 5마리.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 제한 초과"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    BaseResponse<PetResponse> createPet(String userId, CreatePetRequest request);

    @Operation(summary = "내 반려견 목록 조회",
            description = "로그인한 사용자가 등록한 모든 반려견 프로필을 등록순으로 조회합니다.")
    BaseResponse<List<PetResponse>> getMyPets(String userId);

    @Operation(summary = "반려견 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "본인 반려견이 아님"),
            @ApiResponse(responseCode = "404", description = "반려견을 찾을 수 없음")
    })
    BaseResponse<PetResponse> getPet(String userId, @Parameter(description = "반려견 ID") Long petId);

    @Operation(summary = "반려견 정보 수정",
            description = "변경할 필드만 전송하면 됩니다. null인 필드는 변경되지 않습니다.")
    BaseResponse<PetResponse> updatePet(String userId,
                                       @Parameter(description = "반려견 ID") Long petId,
                                       UpdatePetRequest request);

    @Operation(summary = "반려견 삭제 (soft delete)")
    BaseResponse<Void> deletePet(String userId, @Parameter(description = "반려견 ID") Long petId);

    @Operation(summary = "사이즈 추천",
            description = "반려견의 가슴 둘레를 기반으로 특정 상품의 가장 적합한 사이즈를 추천합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "추천 가능한 사이즈 옵션이 없음"),
            @ApiResponse(responseCode = "404", description = "반려견 또는 상품을 찾을 수 없음")
    })
    BaseResponse<SizeRecommendationResponse> recommendSize(
            String userId,
            @Parameter(description = "반려견 ID") Long petId,
            @Parameter(description = "상품 ID") Long productId);

    @Operation(summary = "유사 체형 큐레이션",
            description = "내 반려견과 가슴 둘레가 ±20% 범위인 다른 사용자들이 가장 많이 구매한 상품 TOP 10을 추천합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "404", description = "반려견을 찾을 수 없음")
    })
    BaseResponse<SimilarPetCurationResponse> curateSimilarProducts(
            String userId, @Parameter(description = "반려견 ID") Long petId);
}
