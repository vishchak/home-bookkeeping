package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
