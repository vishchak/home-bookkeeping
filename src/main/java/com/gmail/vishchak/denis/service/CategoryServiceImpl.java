package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public boolean addCategories(Category... categories) {
        if (categories == null) {
            return false;
        }
        categoryRepository.saveAll(Arrays.asList(categories));
        return true;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        category.ifPresent(c -> categoryRepository.deleteById(c.getCategoryId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        if (id == null || id < 1) {
            throw new RuntimeException();
        }
      return categoryRepository.findById(id).get();
    }
}
