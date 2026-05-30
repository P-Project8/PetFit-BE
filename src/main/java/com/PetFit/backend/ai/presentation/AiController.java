package com.PetFit.backend.ai.presentation;

import com.PetFit.backend.ai.application.usecase.GetStyleHistoryUseCase;
import com.PetFit.backend.ai.application.usecase.StyleImageUseCase;
import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleHistoryResponse;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;
import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.AiApi;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController implements AiApi {

    private final StyleImageUseCase styleImageUseCase;
    private final GetStyleHistoryUseCase getStyleHistoryUseCase;

    @PostMapping("/styling")
    @Override
    public BaseResponse<StyleResponse> generateStyling(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody StyleRequest request) {
        return BaseResponse.onSuccess(styleImageUseCase.execute(userId, request));
    }

    @GetMapping("/styling/history")
    @Override
    public BaseResponse<List<StyleHistoryResponse>> getStylingHistory(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(getStyleHistoryUseCase.execute(userId));
    }
}
