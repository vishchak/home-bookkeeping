package com.gmail.vishchak.denis.views.list.goal;

import com.gmail.vishchak.denis.service.GoalServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GoalCheckScheduler {
    private final GoalServiceImpl goalService;

    public GoalCheckScheduler(GoalServiceImpl goalService) {
        this.goalService = goalService;
    }

    @Scheduled(cron = "0 0 3,15 * * ?")
    public void updateGoalStatus() {
        goalService.findAllGoals().forEach(g -> goalService.updateStatus(g.getGoalId()));
        System.out.println("works");
    }
}
