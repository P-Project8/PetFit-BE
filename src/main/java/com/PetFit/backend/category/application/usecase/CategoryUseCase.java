package com.PetFit.backend.category.application.usecase;

import com.PetFit.backend.category.presentation.dto.response.CategoryResponse;
import com.PetFit.backend.category.domain.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryUseCase {

    private final CategoryService categoryService;

    public List<CategoryResponse> getCategories() {
        return categoryService.findAllRootCategories().stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
