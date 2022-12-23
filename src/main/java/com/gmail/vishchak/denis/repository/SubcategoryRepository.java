package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    @Query("select s from Subcategory s where s.category.categoryName = ?1")
    List<Subcategory> findByCategory(String category);

    Optional<Subcategory> findSubcategoryBySubcategoryId(Long id);

    Optional<Subcategory> findBySubcategoryNameLikeIgnoreCase(String name);
}
