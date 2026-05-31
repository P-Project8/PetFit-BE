package com.PetFit.backend.ai.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StyleRequest(
        @NotBlank(message = "반려동물 이미지는 필수입니다.")
        String petImageBase64,

        @NotBlank(message = "옷 이미지는 필수입니다.")
        String clothImageBase64,

        Long productId,

        Long petProfileId
) {}
