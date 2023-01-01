package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query("select g from Goal g where g.user.userId = :id " +
            "and (:goalName is null or (lower(g.goalNote) like lower(concat('%', :goalName, '%')))) " +
            "and g.goalProgress in :goalProgress")
    List<Goal> findGoalsByUserIdAndIfCompleted(Long id,
                                               String goalName,
                                               Set<GoalProgress> goalProgress);

    @Query("select count(g) from Goal g where g.user.userId = ?1")
    Long countGoalsByUserId(Long userId);

}
