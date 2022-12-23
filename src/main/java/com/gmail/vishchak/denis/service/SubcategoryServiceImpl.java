package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.repository.SubcategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {
    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryServiceImpl(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subcategory> findSubcategoryById(Long id) {
        return subcategoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategory> findAllSubcategories() {
        return subcategoryRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        Optional<Subcategory> subcategory = subcategoryRepository.findById(id);
        subcategory.ifPresent(c -> subcategoryRepository.deleteById(c.getSubcategoryId()));
    }

    @Override
    @Transactional
    public boolean addSubcategories(Subcategory... subcategories) {
        if (subcategories == null) {
            return false;
        }
        subcategoryRepository.saveAll(Arrays.asList(subcategories));
        return true;
    }

    @Override
    public List<Subcategory> findByCategory(Category category) {
        return subcategoryRepository.findByCategory(category.getCategoryName());
    }
}
