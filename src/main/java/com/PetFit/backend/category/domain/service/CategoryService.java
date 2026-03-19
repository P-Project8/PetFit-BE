package com.PetFit.backend.category.domain.service;

import com.PetFit.backend.category.domain.entity.Category;
import com.PetFit.backend.category.domain.repository.CategoryRepository;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.CategoryErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RestApiException(CategoryErrorStatus.CATEGORY_NOT_FOUND));
    }

    public List<Category> findAllRootCategories() {
        return categoryRepository.findAllByParentCategoryIsNullAndDeletedAtIsNull();
    }
}
