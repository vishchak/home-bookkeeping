package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);

    List<Transaction> findByNoteLike(String note);

    List<Transaction> findByTransactionDate(Date date);

    @Query("select t from Transaction t where t.transactionDate between ?1 and ?2")
    List<Transaction> findByTransactionDateBetween(Date from, Date to);

    List<Transaction> findByTransactionDateBefore(Date date);

    List<Transaction> findByTransactionDateAfter(Date date);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findBySubcategory(Subcategory subcategory);

    List<Transaction> findByTransactionAmountBetween(Double from, Double to);

    List<Transaction> findByTransactionAmount(Double amount);

    @Query("select t from Transaction t where t.transactionAmount < ?1")
    List<Transaction> findByTransactionAmountLessThan(Double amount);

    List<Transaction> findByTransactionAmountGreaterThan(Double amount);
}
