package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    void deleteTransaction(Long id);

    void addTransaction(Transaction transaction);

    void updateTransaction(Long id, Double amount, String note, Category category, Subcategory subcategory);

    List<Transaction> findSpecificUserTransactions(CustomUser user, Account account, String note, Date from, Date to, Double amount, Category category, Subcategory subcategory, int currentPageNUmber, int itemsPerPage);

    Optional<Transaction> findById(Long id);

    List<Transaction> findAllAccountTransactions(Account account, int currentPageNUmber, int itemsPerPage);

    List<Transaction> findChartTransactions(CustomUser user, Date from, Date to, Category category);

    List<Transaction> findAllUSerTransactions(CustomUser user, int currentPageNUmber, int itemsPerPage);

    Long getPageCount(CustomUser user, Account account, int itemsPerPage);

    Long getTransactionCountByCategory(CustomUser user, Category category);
}
