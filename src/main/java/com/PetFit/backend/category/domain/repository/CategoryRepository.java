package com.PetFit.backend.category.domain.repository;

import com.PetFit.backend.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByParentCategoryIsNullAndDeletedAtIsNull();
}
