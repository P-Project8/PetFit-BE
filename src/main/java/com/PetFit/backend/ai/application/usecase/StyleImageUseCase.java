package com.PetFit.backend.ai.application.usecase;

import com.PetFit.backend.ai.domain.service.GeminiAIService;
import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StyleImageUseCase {

    private final GeminiAIService geminiAIService;

    public StyleResponse execute(StyleRequest request) {
        String resultImageBase64 = geminiAIService.generateStyledImage(
                request.petImageBase64(),
                request.clothImageBase64()
        );
        return new StyleResponse(resultImageBase64);
    }
}
