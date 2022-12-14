package com.gmail.vishchak.denis.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "transactionId")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long transactionId;

    @Column(name = "amount", nullable = false)
    private Double transactionAmount;

    private String note;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    public Transaction(Double transactionAmount, String note, Date transactionDate, Account account, Category category, Subcategory subcategory) {
        this.transactionAmount = transactionAmount;
        this.note = note;
        this.transactionDate = transactionDate;
        this.account = account;
        this.category = category;
        this.subcategory = subcategory;
    }
}
