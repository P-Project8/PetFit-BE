package com.PetFit.backend.ai.application.usecase;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import com.PetFit.backend.ai.domain.service.AiStylingService;
import com.PetFit.backend.ai.domain.service.ImageProcessingService;
import com.PetFit.backend.ai.presentation.dto.response.StyleDownloadResponse;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.AiErrorStatus;
import com.PetFit.backend.subscription.domain.entity.Subscription;
import com.PetFit.backend.subscription.domain.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * AI 스타일링 결과 다운로드 (구독 등급별 차별화).
 *
 * PDF 3순위 정책:
 * - FREE: 512px 리사이즈 + "PetFit Free" 워터마크
 * - PREMIUM: 원본 해상도 그대로 + 워터마크 없음
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DownloadStyleUseCase {

    private final AiStylingService aiStylingService;
    private final ImageProcessingService imageProcessingService;
    private final SubscriptionService subscriptionService;

    public StyleDownloadResponse execute(String userId, Long stylingId) {
        AiStyling styling = aiStylingService.findOwnedOrThrow(stylingId, userId);

        if (styling.getResultImageUrl() == null
                || !"COMPLETED".equals(styling.getStatus())) {
            throw new RestApiException(AiErrorStatus.AI_RESULT_NOT_READY);
        }

        // 1) 원본 이미지 S3에서 가져오기
        byte[] originalBytes = imageProcessingService.fetch(styling.getResultImageUrl());

        // 2) 구독 등급 확인
        Subscription subscription = subscriptionService.getOrCreateActive(userId);
        boolean isPremium = subscription.isPremium();

        // 3) 등급에 따라 처리
        byte[] outputBytes = isPremium
                ? originalBytes
                : imageProcessingService.applyFreeTierProcessing(originalBytes);

        // 4) 결과 크기 측정
        int[] dimensions = readDimensions(outputBytes);

        return new StyleDownloadResponse(
                styling.getId(),
                "image/png",
                Base64.getEncoder().encodeToString(outputBytes),
                isPremium,
                subscription.getPlan(),
                dimensions[0],
                dimensions[1]
        );
    }

    private int[] readDimensions(byte[] bytes) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            if (img == null) return new int[]{0, 0};
            return new int[]{img.getWidth(), img.getHeight()};
        } catch (IOException e) {
            return new int[]{0, 0};
        }
    }
}
