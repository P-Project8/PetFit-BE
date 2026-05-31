package com.PetFit.backend.ai.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.AiErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAIService {

    private final RestTemplate restTemplate;

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-2.0-flash-exp}")
    private String model;

    private static final String BASE_PROMPT = """
            Analyze the two images provided.
            The goal is to visualize the clothing (2nd image) on the pet (1st image).

            Condition for refusal:
            - Only if the first image is clearly NOT a living animal (e.g. it's a building, landscape, text only).
            - OR if the second image is clearly NOT a fashion item.

            If these specific refusal conditions are met, return: {"error": "INVALID_INPUT"}

            Otherwise, PLEASE PROCEED with the styling generation even if you are unsure.
            Put the clothing from the second image onto the pet in the first image naturally.
            Maintain the clothing's pattern and color. Show a full-body shot of the pet wearing the clothes.
            """;

    public String generateStyledImage(String petImageBase64, String clothesImageBase64) {
        return generateStyledImage(petImageBase64, clothesImageBase64, BASE_PROMPT);
    }

    /**
     * 반려견 체형 정보를 함께 입력해 사이즈/실루엣을 더 정확히 렌더링한다.
     */
    public String generateStyledImageWithProfile(String petImageBase64, String clothesImageBase64,
                                                 String breed, Integer age, Double weight,
                                                 Double neckSize, Double chestSize, Double backLength) {
        String profileLines = String.format("""

                Pet profile (use this to ensure realistic body proportions and fit):
                - Breed: %s
                - Age: %d years
                - Weight: %.1f kg
                - Neck circumference: %.1f cm
                - Chest circumference: %.1f cm
                - Back length: %.1f cm

                Make sure the clothing wraps the body in a way that respects these dimensions.
                Avoid distorting the pet's natural body shape.
                """, breed, age, weight, neckSize, chestSize, backLength);
        return generateStyledImage(petImageBase64, clothesImageBase64, BASE_PROMPT + profileLines);
    }

    private String generateStyledImage(String petImageBase64, String clothesImageBase64, String prompt) {
        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey
        );

        Map<String, Object> requestBody = buildRequestBody(petImageBase64, clothesImageBase64, prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return extractImageFromResponse(response.getBody());
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            throw new RestApiException(AiErrorStatus.AI_SERVICE_ERROR);
        }
    }

    private Map<String, Object> buildRequestBody(String petImageBase64, String clothesImageBase64, String prompt) {
        // data:image/...;base64, 접두사 제거
        petImageBase64 = stripDataPrefix(petImageBase64);
        clothesImageBase64 = stripDataPrefix(clothesImageBase64);

        List<Map<String, Object>> parts = new ArrayList<>();

        parts.add(Map.of("text", prompt));

        parts.add(Map.of(
                "inline_data", Map.of(
                        "mime_type", "image/jpeg",
                        "data", petImageBase64
                )
        ));

        parts.add(Map.of(
                "inline_data", Map.of(
                        "mime_type", "image/jpeg",
                        "data", clothesImageBase64
                )
        ));

        Map<String, Object> content = Map.of("parts", parts);

        Map<String, Object> generationConfig = Map.of(
                "responseModalities", List.of("TEXT", "IMAGE")
        );

        return Map.of(
                "contents", List.of(content),
                "generationConfig", generationConfig
        );
    }

    private String stripDataPrefix(String base64) {
        if (base64 != null && base64.contains(",")) {
            return base64.substring(base64.indexOf(",") + 1);
        }
        return base64;
    }

    @SuppressWarnings("unchecked")
    private String extractImageFromResponse(Map<String, Object> responseBody) {
        if (responseBody == null) {
            throw new RestApiException(AiErrorStatus.AI_EMPTY_RESPONSE);
        }

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            // 텍스트 파트에서 에러 체크
            for (Map<String, Object> part : parts) {
                if (part.containsKey("text")) {
                    String text = (String) part.get("text");
                    if (text.contains("INVALID_INPUT")) {
                        throw new RestApiException(AiErrorStatus.AI_INVALID_INPUT);
                    }
                }
            }

            // 이미지 파트 추출
            for (Map<String, Object> part : parts) {
                if (part.containsKey("inline_data")) {
                    Map<String, String> inlineData = (Map<String, String>) part.get("inline_data");
                    String mimeType = inlineData.get("mimeType");
                    String data = inlineData.get("data");
                    return "data:" + mimeType + ";base64," + data;
                }
            }

            throw new RestApiException(AiErrorStatus.AI_NO_IMAGE_GENERATED);
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", e.getMessage());
            throw new RestApiException(AiErrorStatus.AI_RESPONSE_PARSE_ERROR);
        }
    }
}
