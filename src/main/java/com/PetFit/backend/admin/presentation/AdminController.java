package com.PetFit.backend.admin.presentation;

import com.PetFit.backend.admin.application.usecase.AdminStatsUseCase;
import com.PetFit.backend.admin.presentation.dto.response.AdminStatsResponse;
import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.AdminApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController implements AdminApi {

    private final AdminStatsUseCase adminStatsUseCase;

    @GetMapping("/stats")
    @Override
    public BaseResponse<AdminStatsResponse> getStats(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(adminStatsUseCase.collect(userId));
    }
}
