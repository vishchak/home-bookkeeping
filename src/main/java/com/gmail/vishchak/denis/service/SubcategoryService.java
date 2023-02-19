package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;

import java.util.List;
import java.util.Optional;

public interface SubcategoryService {

    List<Subcategory> findAllSubcategories();

    List<Subcategory> findByCategory(Category category);

    void addSubcategories(Subcategory... subcategories);

    Optional<Subcategory>findBySubcategoryName(String subcategoryName);
}
