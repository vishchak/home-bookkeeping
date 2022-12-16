package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;

import java.util.List;

public interface CategoryService {
    Category findById(Long id);
    List<Category> findAll();

    boolean addCategories(Category... categories);

    void deleteCategory(Long id);
}
