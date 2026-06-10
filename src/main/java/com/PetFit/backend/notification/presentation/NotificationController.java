package com.PetFit.backend.notification.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.NotificationApi;
import com.PetFit.backend.notification.application.usecase.NotificationUseCase;
import com.PetFit.backend.notification.presentation.dto.response.NotificationResponse;
import com.PetFit.backend.notification.presentation.dto.response.UnreadCountResponse;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    @Override
    public BaseResponse<Page<NotificationResponse>> getMyNotifications(
            @Parameter(hidden = true) @CurrentUser String userId,
            Pageable pageable) {
        return BaseResponse.onSuccess(notificationUseCase.findMy(userId, pageable));
    }

    @GetMapping("/unread-count")
    @Override
    public BaseResponse<UnreadCountResponse> getUnreadCount(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(notificationUseCase.getUnreadCount(userId));
    }

    @PatchMapping("/{notificationId}/read")
    @Override
    public BaseResponse<NotificationResponse> markRead(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long notificationId) {
        return BaseResponse.onSuccess(notificationUseCase.markRead(userId, notificationId));
    }

    @PatchMapping("/read-all")
    @Override
    public BaseResponse<Integer> markAllRead(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(notificationUseCase.markAllRead(userId));
    }
}
