package com.PetFit.backend.category.presentation;

import com.PetFit.backend.category.presentation.dto.response.CategoryResponse;
import com.PetFit.backend.category.application.usecase.CategoryUseCase;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.CategoryApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController implements CategoryApi {

    private final CategoryUseCase categoryUseCase;

    @GetMapping
    @Override
    public BaseResponse<List<CategoryResponse>> getCategories() {
        return BaseResponse.onSuccess(categoryUseCase.getCategories());
    }
}
