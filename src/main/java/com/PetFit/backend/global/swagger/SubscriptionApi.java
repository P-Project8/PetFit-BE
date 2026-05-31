package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.subscription.presentation.dto.response.CreditStatusResponse;
import com.PetFit.backend.subscription.presentation.dto.response.SubscriptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Subscription", description = "구독 & AI 크레딧 API")
public interface SubscriptionApi {

    @Operation(summary = "내 구독 조회",
            description = "현재 ACTIVE 구독을 반환. 없으면 FREE 플랜이 자동 생성된다.")
    BaseResponse<SubscriptionResponse> getMy(String userId);

    @Operation(summary = "프리미엄 업그레이드 (프로토타입)",
            description = "결제 mock — 즉시 30일 PREMIUM 활성화. 이미 PREMIUM이면 400.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업그레이드 성공"),
            @ApiResponse(responseCode = "400", description = "이미 PREMIUM 상태")
    })
    BaseResponse<SubscriptionResponse> upgrade(String userId);

    @Operation(summary = "구독 취소",
            description = "PREMIUM을 즉시 취소하고 FREE 플랜으로 전환. FREE는 취소 불가.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공 (FREE로 전환됨)"),
            @ApiResponse(responseCode = "400", description = "FREE 플랜은 취소 불가")
    })
    BaseResponse<SubscriptionResponse> cancel(String userId);

    @Operation(summary = "AI 크레딧 현황 조회",
            description = "이번 달 AI 스타일링 사용량과 잔여 횟수. PREMIUM은 unlimited=true, 한도/잔여=-1.")
    BaseResponse<CreditStatusResponse> getCredits(String userId);
}
