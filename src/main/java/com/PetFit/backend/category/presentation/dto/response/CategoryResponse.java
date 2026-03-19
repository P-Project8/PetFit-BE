package com.PetFit.backend.category.presentation.dto.response;

import com.PetFit.backend.category.domain.entity.Category;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        List<CategoryResponse> children
) {
    public static CategoryResponse from(Category category) {
        List<CategoryResponse> childResponses = category.getChildren().stream()
                .filter(c -> !c.isDeleted())
                .map(CategoryResponse::from)
                .toList();

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                childResponses
        );
    }
}
