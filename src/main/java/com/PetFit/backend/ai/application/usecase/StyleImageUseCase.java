package com.PetFit.backend.ai.application.usecase;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import com.PetFit.backend.ai.domain.service.AiStylingService;
import com.PetFit.backend.ai.domain.service.GeminiAIService;
import com.PetFit.backend.ai.presentation.dto.request.StyleRequest;
import com.PetFit.backend.ai.presentation.dto.response.StyleResponse;
import com.PetFit.backend.file.domain.service.FileStorageService;
import com.PetFit.backend.pet.domain.entity.PetProfile;
import com.PetFit.backend.pet.domain.service.PetService;
import com.PetFit.backend.subscription.domain.service.CreditService;

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
    private final PetService petService;
    private final CreditService creditService;

    public StyleResponse execute(String userId, StyleRequest request) {
        // 0. 크레딧 검증 (FREE 월 3회, PREMIUM 무제한) — 초과 시 402 발생
        creditService.assertCanConsume(userId);

        // 1. Pre-save: 입력 이미지를 먼저 S3에 저장
        String petImageUrl = fileStorageService.uploadBase64(
                request.petImageBase64(), STYLING_FOLDER + "/inputs", userId, "image/jpeg"
        );
        String clothImageUrl = fileStorageService.uploadBase64(
                request.clothImageBase64(), STYLING_FOLDER + "/inputs", userId, "image/jpeg"
        );

        // 2. PetProfile 조회 (옵션)
        PetProfile petProfile = null;
        if (request.petProfileId() != null) {
            petProfile = petService.findOwnedOrThrow(request.petProfileId(), userId);
        }

        // 3. AiStyling 엔티티 생성 (PENDING 상태)
        AiStyling styling = AiStyling.builder()
                .userId(userId)
                .productId(request.productId())
                .petImageUrl(petImageUrl)
                .clothImageUrl(clothImageUrl)
                .build();
        styling = aiStylingService.save(styling);

        try {
            // 4. Gemini AI 호출 (체형 데이터가 있으면 프롬프트 강화)
            String resultImageBase64 = petProfile != null
                    ? geminiAIService.generateStyledImageWithProfile(
                            request.petImageBase64(),
                            request.clothImageBase64(),
                            petProfile.getBreed(),
                            petProfile.getAge(),
                            petProfile.getWeight(),
                            petProfile.getNeckSize(),
                            petProfile.getChestSize(),
                            petProfile.getBackLength())
                    : geminiAIService.generateStyledImage(
                            request.petImageBase64(),
                            request.clothImageBase64());

            // 5. 결과를 S3에 저장
            String resultImageUrl = fileStorageService.uploadBase64(
                    resultImageBase64, STYLING_FOLDER + "/results", userId, "image/png"
            );

            // 6. AiStyling 완료 상태로 업데이트
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
