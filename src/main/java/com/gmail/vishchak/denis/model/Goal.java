package com.gmail.vishchak.denis.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "goalId")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long goalId;

    @NotNull
    @Column(name = "note")
    private String goalNote;

    @NotNull
    private Double goalAmount;

    private Double currentAmount;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date finishDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    @Enumerated(EnumType.STRING)
    private GoalProgress goalProgress;

    public Goal(String goalNote, Double goalAmount, Date finishDate, CustomUser user) {
        this.goalNote = goalNote;
        this.goalAmount = goalAmount;
        this.startDate = new Date();
        this.finishDate = finishDate;
        this.user = user;
        this.currentAmount = 0D;
        this.goalProgress = GoalProgress.CURRENT;
    }
}
