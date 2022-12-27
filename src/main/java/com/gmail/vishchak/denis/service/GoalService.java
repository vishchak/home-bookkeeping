package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Goal;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GoalService {
    void deleteGoal(Long id);

    void updateGoal(Long goalId, String goalNOte, Double amount, Double currentAmount, Date finishDate, Boolean ifCompleted);

    List<Goal> findUserGoals(Long userId, Boolean ifCompleted);

    Long countUserGoals(CurrentUser user);

    Optional<Goal> findById(Long goalId);
}
