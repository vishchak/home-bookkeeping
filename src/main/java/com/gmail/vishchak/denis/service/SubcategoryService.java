package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;

import java.util.List;
import java.util.Optional;

public interface SubcategoryService {
    Optional<Subcategory> findSubcategoryById(Long id);

    List<Subcategory> findAllCategories();

    boolean addSubcategories(Subcategory... subcategories);

    void deleteSubcategory(Long id);
}
