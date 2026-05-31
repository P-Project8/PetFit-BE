package com.PetFit.backend.gallery.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGalleryRequest(
        @NotBlank(message = "이미지 URL은 필수입니다.") String imageUrl,
        @Size(max = 500, message = "설명은 최대 500자입니다.") String caption,
        Long petProfileId,
        Long productId,
        Long stylingId
) {}
