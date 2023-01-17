package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GoalRepository extends PagingAndSortingRepository<Goal, Long> {

    @Query("select g from Goal g where g.user.userId = :id " +
            "and (:goalName is null or (lower(g.goalNote) like lower(concat('%', :goalName, '%')))) " +
            "and g.goalProgress in :goalProgress")
    List<Goal> findGoalsByUserIdAndIfCompleted(Long id,
                                               String goalName,
                                               Set<GoalProgress> goalProgress,
                                               Pageable pageable);

    @Query("select count(g) from Goal g where g.user = ?1")
    Long countGoalsByUser(CustomUser user);

}
