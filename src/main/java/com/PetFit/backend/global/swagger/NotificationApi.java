package com.PetFit.backend.global.swagger;

import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.notification.presentation.dto.response.NotificationResponse;
import com.PetFit.backend.notification.presentation.dto.response.UnreadCountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Notification", description = "알림 API (인증 필수)")
public interface NotificationApi {

    @Operation(summary = "내 알림 목록 조회 (최신순)",
            description = """
                    알림 종류:
                    - GALLERY_LIKED: 내 갤러리에 좋아요
                    - GALLERY_COMMENTED: 내 갤러리에 댓글
                    - CREDIT_WARNING: AI 크레딧 임계점 도달 (FREE 2/3 사용)
                    - CREDIT_EXHAUSTED: 크레딧 모두 사용
                    - SUBSCRIPTION_EXPIRED: PREMIUM 구독 만료
                    """)
    BaseResponse<Page<NotificationResponse>> getMyNotifications(String userId, Pageable pageable);

    @Operation(summary = "읽지 않은 알림 개수",
            description = "헤더 뱃지 표시용. 클라이언트가 주기적으로 폴링 가능.")
    BaseResponse<UnreadCountResponse> getUnreadCount(String userId);

    @Operation(summary = "알림 1건 읽음 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "본인 알림 아님"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    BaseResponse<NotificationResponse> markRead(
            String userId, @Parameter(description = "알림 ID") Long notificationId);

    @Operation(summary = "모든 알림 읽음 처리",
            description = "성공 시 처리된 알림 개수 반환.")
    BaseResponse<Integer> markAllRead(String userId);
}
