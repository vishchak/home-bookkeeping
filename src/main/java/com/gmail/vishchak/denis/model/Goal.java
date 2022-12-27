package com.gmail.vishchak.denis.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @Column(name = "amount")
    private Double goalAmount;

    private Boolean ifCompleted;
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date finishDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CurrentUser user;

    public Goal(String goalNote, Double goalAmount, Date startDate, Date finishDate, CurrentUser user) {
        this.goalNote = goalNote;
        this.goalAmount = goalAmount;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.user = user;
        this.ifCompleted = false;
    }
}
