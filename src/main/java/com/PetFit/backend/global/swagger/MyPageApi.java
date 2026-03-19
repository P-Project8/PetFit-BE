package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.mypage.presentation.dto.response.MyPageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "마이페이지", description = "마이페이지 API")
public interface MyPageApi extends BaseApi {

    @Operation(summary = "마이페이지 조회", description = "마이페이지 정보를 조회합니다. (주문수, 리뷰수, 찜수, 최근 주문)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    BaseResponse<MyPageResponse> getMyPage(String userId);
}
