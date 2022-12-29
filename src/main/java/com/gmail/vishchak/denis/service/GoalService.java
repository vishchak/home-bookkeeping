package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Goal;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GoalService {
    void deleteGoal(Long id);

    void updateGoal(Long goalId, String goalNOte, Double amount, Date finishDate);

    List<Goal> findUserGoals(Long userId, Boolean ifCompleted);

    Long countUserGoals(Long userId);

    Optional<Goal> findById(Long goalId);

    boolean addGoal(Goal goal);

    void addMoney(Long goalId, Double amount, Account currentAccount);
}
