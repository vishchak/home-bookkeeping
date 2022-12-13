package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
