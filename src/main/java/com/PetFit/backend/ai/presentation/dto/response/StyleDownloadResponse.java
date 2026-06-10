package com.PetFit.backend.ai.presentation.dto.response;

/**
 * AI 스타일링 결과 다운로드 응답.
 *
 * 구독 등급별 차별화:
 * - FREE: 512px 다운사이즈 + 워터마크 합성
 * - PREMIUM: 원본 해상도, 워터마크 없음
 */
public record StyleDownloadResponse(
        Long stylingId,
        String mimeType,         // 항상 "image/png"
        String imageBase64,      // 다운로드용 base64
        boolean isPremiumQuality,// true면 원본 화질, false면 FREE 처리됨
        String plan,             // 호출 시점 구독 플랜 (FREE / PREMIUM)
        int width,               // 결과 이미지 가로 (px)
        int height               // 결과 이미지 세로 (px)
) {}
