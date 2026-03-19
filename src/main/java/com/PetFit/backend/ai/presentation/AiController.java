package com.PetFit.backend.ai.presentation;

import com.PetFit.backend.ai.application.usecase.StyleImageUseCase;
import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;
import com.PetFit.backend.global.common.BaseResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final StyleImageUseCase styleImageUseCase;

    @PostMapping("/styling")
    public BaseResponse<StyleResponse> generateStyling(
            @Valid @RequestBody StyleRequest request) {
        return BaseResponse.onSuccess(styleImageUseCase.execute(request));
    }
}
