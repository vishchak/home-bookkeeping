package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.model.enums.GoalProgress;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GoalService {
    void deleteGoal(Long id);

    void updateGoal(Long goalId, String goalNOte, Double amount, Date finishDate);

    List<Goal> findUserGoals(Long userId, String goalName, Set<GoalProgress> goalProgress, int currentPageNumber, int itemsPerPage);

    Long getPageCount(CustomUser user, int itemsPerPage);

    Optional<Goal> findById(Long goalId);

    void addGoal(Goal goal);

    void addMoney(Long goalId, Double amount, Account currentAccount);

    void updateStatus(Long id);
}
