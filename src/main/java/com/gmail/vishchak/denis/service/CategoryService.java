package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<Category> findCategoryById(Long id);

    List<Category> findAllCategories();

    void addCategories(Category... categories);

    void deleteCategory(Long id);
}
