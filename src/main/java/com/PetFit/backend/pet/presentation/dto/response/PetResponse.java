package com.PetFit.backend.pet.presentation.dto.response;

import com.PetFit.backend.pet.domain.entity.PetProfile;

import java.time.LocalDateTime;

public record PetResponse(
        Long id,
        String name,
        String breed,
        Integer age,
        Double weight,
        Double neckSize,
        Double chestSize,
        Double backLength,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PetResponse from(PetProfile pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getAge(),
                pet.getWeight(),
                pet.getNeckSize(),
                pet.getChestSize(),
                pet.getBackLength(),
                pet.getImageUrl(),
                pet.getCreatedAt(),
                pet.getUpdatedAt()
        );
    }
}
