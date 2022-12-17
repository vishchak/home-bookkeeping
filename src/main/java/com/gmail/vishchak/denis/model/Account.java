package com.gmail.vishchak.denis.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "accountId")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long accountId;

    @Column(name = "name")
    private String accountName;

    @Column(name = "amount", nullable = false)
    private Double accountAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CurrentUser user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public Account(String accountName, Double accountAmount, CurrentUser user) {
        this.accountName = accountName;
        this.accountAmount = accountAmount;
        this.user = user;
    }
}
