package com.PetFit.backend.global.swagger;

import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin", description = "관리자 API (admin.user-ids 설정에 등록된 사용자만 접근 가능)")
public interface AdminApi {

    @Operation(summary = "통계 대시보드",
            description = """
                    관리자 화면/발표 시연용 단일 호출 통계. 모든 도메인의 핵심 지표를 한 번에 반환.
                    - 사용자: 총/주간/월간 신규
                    - AI: 호출/성공/실패/성공률
                    - 주문: 누적/주간/매출
                    - 갤러리: 게시물/좋아요/댓글
                    - 구독: FREE/PREMIUM 분포 + 프리미엄 비율
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    BaseResponse<AdminStatsResponse> getStats(String userId);
}
