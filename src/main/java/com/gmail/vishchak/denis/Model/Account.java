package com.gmail.vishchak.denis.Model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}
