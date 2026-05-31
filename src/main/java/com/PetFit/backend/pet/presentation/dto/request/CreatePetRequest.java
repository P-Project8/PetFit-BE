package com.PetFit.backend.pet.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePetRequest(
        @NotBlank(message = "반려견 이름은 필수입니다.") String name,
        @NotBlank(message = "견종은 필수입니다.") String breed,
        @NotNull @Positive(message = "나이는 0보다 커야 합니다.") Integer age,
        @NotNull @Positive(message = "체중은 0보다 커야 합니다.") Double weight,
        @NotNull @Positive(message = "목 둘레는 0보다 커야 합니다.") Double neckSize,
        @NotNull @Positive(message = "가슴 둘레는 0보다 커야 합니다.") Double chestSize,
        @NotNull @Positive(message = "등 길이는 0보다 커야 합니다.") Double backLength,
        String imageUrl
) {}
