package com.PetFit.backend.pet.presentation.dto.request;

import jakarta.validation.constraints.Positive;

public record UpdatePetRequest(
        String name,
        String breed,
        @Positive(message = "나이는 0보다 커야 합니다.") Integer age,
        @Positive(message = "체중은 0보다 커야 합니다.") Double weight,
        @Positive(message = "목 둘레는 0보다 커야 합니다.") Double neckSize,
        @Positive(message = "가슴 둘레는 0보다 커야 합니다.") Double chestSize,
        @Positive(message = "등 길이는 0보다 커야 합니다.") Double backLength,
        String imageUrl
) {}
