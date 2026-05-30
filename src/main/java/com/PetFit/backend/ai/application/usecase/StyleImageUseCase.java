package com.PetFit.backend.ai.application.usecase;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import com.PetFit.backend.ai.domain.service.AiStylingService;
import com.PetFit.backend.ai.domain.service.GeminiAIService;
import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;
import com.PetFit.backend.file.domain.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StyleImageUseCase {

    private static final String STYLING_FOLDER = "styling";

    private final GeminiAIService geminiAIService;
    private final FileStorageService fileStorageService;
    private final AiStylingService aiStylingService;

    public StyleResponse execute(String userId, StyleRequest request) {
        // 1. Pre-save: 입력 이미지를 먼저 S3에 저장
        String petImageUrl = fileStorageService.uploadBase64(
                request.petImageBase64(), STYLING_FOLDER + "/inputs", userId, "image/jpeg"
        );
        String clothImageUrl = fileStorageService.uploadBase64(
                request.clothImageBase64(), STYLING_FOLDER + "/inputs", userId, "image/jpeg"
        );

        // 2. AiStyling 엔티티 생성 (PENDING 상태)
        AiStyling styling = AiStyling.builder()
                .userId(userId)
                .productId(request.productId())
                .petImageUrl(petImageUrl)
                .clothImageUrl(clothImageUrl)
                .build();
        styling = aiStylingService.save(styling);

        try {
            // 3. Gemini AI 호출
            String resultImageBase64 = geminiAIService.generateStyledImage(
                    request.petImageBase64(),
                    request.clothImageBase64()
            );

            // 4. 결과를 S3에 저장
            String resultImageUrl = fileStorageService.uploadBase64(
                    resultImageBase64, STYLING_FOLDER + "/results", userId, "image/png"
            );

            // 5. AiStyling 완료 상태로 업데이트
            styling.complete(resultImageUrl);
            aiStylingService.save(styling);

            return new StyleResponse(styling.getId(), resultImageUrl, resultImageBase64);
        } catch (Exception e) {
            log.error("AI 스타일링 실패: userId={}, stylingId={}", userId, styling.getId(), e);
            styling.fail();
            aiStylingService.save(styling);
            throw e;
        }
    }
}
