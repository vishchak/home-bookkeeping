package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import com.gmail.vishchak.denis.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final TransactionService transactionService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;

    public GoalServiceImpl(GoalRepository goalRepository, TransactionService transactionService, CategoryServiceImpl categoryService, SubcategoryServiceImpl subcategoryService, AccountServiceImpl accountService) {
        this.goalRepository = goalRepository;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
    }

    @Override
    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateGoal(Long goalId, String goalNote, Double amount, Date finishDate) {
        Optional<Goal> goal = goalRepository.findById(goalId);
        goal.ifPresent(g -> {
            if (goalNote != null) {
                if (!(goalNote.isEmpty() && goalNote.isBlank())) {
                    g.setGoalNote(goalNote);
                }
            }
            if (amount != null) {
                if (amount > 0) {
                    g.setGoalAmount(amount);
                }
            }
            if (finishDate != null) {
                if (finishDate.getTime() >= g.getStartDate().getTime())
                    g.setFinishDate(finishDate);
            }
            if (g.getCurrentAmount() >= g.getGoalAmount()) {
                g.setGoalProgress(GoalProgress.COMPLETED);
            } else {
                g.setGoalProgress(GoalProgress.CURRENT);
            }

            goalRepository.save(g);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goal> findUserGoals(Long userId, String goalName, Set<GoalProgress> goalProgress) {
        return goalRepository.findGoalsByUserIdAndIfCompleted(userId, goalName, goalProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUserGoals(Long userId) {
        return goalRepository.countGoalsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Goal> findById(Long goalId) {
        return goalRepository.findById(goalId);
    }

    @Override
    @Transactional
    public boolean addGoal(Goal goal) {
        if (goal.getGoalAmount() <= 0 || goal.getUser() == null) {
            return false;
        }
        if (goal.getGoalNote().isEmpty() || goal.getGoalNote().isBlank() || goal.getGoalNote() == null) {
            goal.setGoalNote("-");
        }
        goalRepository.save(goal);
        return true;
    }

    @Override
    @Transactional
    public void addMoney(Long goalId, Double amount, Account currentAccount) {
        Optional<Goal> goal = goalRepository.findById(goalId);
        goal.ifPresent(g -> {
            if (amount == null || amount<=0) {
                return;
            }
            Optional<Category> category = categoryService.findCategoryById(3L);
            Optional<Subcategory> subcategory = subcategoryService.findSubcategoryById(15L);

            if ((g.getCurrentAmount() + amount) > g.getGoalAmount()) {
                double maxAmount = g.getGoalAmount() - g.getCurrentAmount();
                ifPossible(maxAmount, g, category, subcategory, currentAccount);
            } else {
                ifPossible(amount, g, category, subcategory, currentAccount);
            }
        });
    }

    private void ifPossible(Double amount, Goal g, Optional<Category> category, Optional<Subcategory> subcategory, Account currentAccount) {
        g.setCurrentAmount(g.getCurrentAmount() + amount);
        if (g.getCurrentAmount() >= g.getGoalAmount()) {
            g.setGoalProgress(GoalProgress.COMPLETED);
        }
        goalRepository.save(g);

        if (category.isPresent() && subcategory.isPresent()) {
            transactionService.addTransaction(new Transaction(amount, "Goal " + g.getGoalNote(), new Date(), currentAccount, category.get(), subcategory.get()));
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id) {
        Optional<Goal> goal = goalRepository.findById(id);
        goal.ifPresent(g -> {
            if (!g.getGoalProgress().equals(GoalProgress.COMPLETED)) {
                g.setGoalProgress(GoalProgress.FAILED);
                goalRepository.save(g);
            }
        });
    }
}
