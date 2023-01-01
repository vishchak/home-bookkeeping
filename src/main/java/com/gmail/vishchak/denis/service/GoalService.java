package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.model.enums.GoalProgress;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GoalService {
    void deleteGoal(Long id);

    void updateGoal(Long goalId, String goalNOte, Double amount, Date finishDate);

    List<Goal> findUserGoals(Long userId, String goalName, Set<GoalProgress> goalProgress);

    Long countUserGoals(Long userId);

    Optional<Goal> findById(Long goalId);

    boolean addGoal(Goal goal);

    void addMoney(Long goalId, Double amount, Account currentAccount);
}
