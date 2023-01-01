package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    void deleteTransaction(Long id);

    void addTransaction(Transaction transaction);

    void updateTransaction(Long id, Double amount, String note, Category category, Subcategory subcategory);

    List<Transaction> findAccountTransactions(Account account, String note, Date from, Date to, Double amount, Category category, Subcategory subcategory, int currentPageNUmber, int itemsPerPage);

    Long getPageCount(Account account, int itemsPerPage);

    Optional<Transaction> findById(Long id);

    List<Transaction> findAllTransactionByAccount(Account account, int currentPageNUmber, int itemsPerPage);

    List<Transaction> findChartTransactions(Account account, Date from, Date to, Category category);
}
