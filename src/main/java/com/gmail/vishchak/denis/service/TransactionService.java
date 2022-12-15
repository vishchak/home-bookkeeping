package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    void deleteTransactions(List<Long> ids);

    boolean addTransaction(Account account, Double amount, String note, Date date, Category category, Subcategory subcategory);

    void updateTransaction(Long id, Double amount, String note, Category category, Subcategory subcategory);

    List<Transaction> findAccountTransactions(Account account);

    List<Transaction> findTransactionsByNotes(String note);

    List<Transaction> findTransactionsByDate(Date date);

    List<Transaction> findTransactionsByDateBefore(Date date);

    List<Transaction> findTransactionsByDateAfter(Date date);

    List<Transaction> findTransactionsByCategory(Category category);

    List<Transaction> findTransactionsBySubcategory(Subcategory subcategory);

    List<Transaction> findTransactionsByAmount(Double amount);

    List<Transaction> findTransactionsByAmountGreaterThan(Double amount);

    List<Transaction> findTransactionsByAmountLEssThan(Double amount);

    List<Transaction> findTransactionsByAmountBetween(Double from, Double to);

    Long countTransactions(Account account);

}
