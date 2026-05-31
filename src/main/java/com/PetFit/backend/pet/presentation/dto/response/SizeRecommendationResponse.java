package com.PetFit.backend.pet.presentation.dto.response;

import java.util.List;

public record SizeRecommendationResponse(
        Long petId,
        String petName,
        Long productId,
        String productName,
        String recommendedSize,
        String reasoning,
        List<OptionFit> optionFits
) {
    public record OptionFit(
            String size,
            String fit,         // "TOO_SMALL", "FIT", "LOOSE", "TOO_LARGE"
            String description
    ) {}
}
