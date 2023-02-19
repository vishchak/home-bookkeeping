package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.repository.SubcategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Subcategory> findAllSubcategories() {
        return subcategoryRepository.findAll();
    }

    @Override
    @Transactional
    public void addSubcategories(Subcategory... subcategories) {
        for (Subcategory s :
                subcategories) {
            if (subcategoryRepository.findBySubcategoryNameLikeIgnoreCase(s.getSubcategoryName()).isEmpty()) {
                subcategoryRepository.save(s);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subcategory> findBySubcategoryName(String subcategoryName) {
        return subcategoryRepository.findBySubcategoryNameLikeIgnoreCase(subcategoryName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategory> findByCategory(Category category) {
        return subcategoryRepository.findByCategory(category);
    }
}
