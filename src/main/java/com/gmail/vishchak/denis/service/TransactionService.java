package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    void deleteTransaction(Long id);

    boolean addTransaction(Transaction transaction);

    void updateTransaction(Long id, Double amount, String note, Category category, Subcategory subcategory);

    List<Transaction> findAccountTransactions(Account account, String note, Date from, Date to);

    Long countTransactions(Account account);
}
