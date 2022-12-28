package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query("select g from Goal g where g.user.userId = :id " +
            "and (:ifCompleted is null or (g.ifCompleted = :ifCompleted))")
    List<Goal> findGoalsByUserIdAndIfCompleted(Long id, Boolean ifCompleted);

    @Query("select count(g) from Goal g where g.user.userId = ?1")
    Long countGoalsByUserId(Long userId);
}