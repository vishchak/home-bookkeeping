package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("select t from Transaction t where t.account.accountId =:accountId")
    List<Transaction> findByAccount(@Param("accountId") Long accountId);

    @Query("select t from Transaction t " +
            "where t.account.accountId =:accountId and " +
            "lower(t.note) like lower(concat('%', :note, '%'))")
    List<Transaction> findByAccountIdAndNoteLike(@Param("accountId") Long accountId,
                                                 @Param("note") String note);

    @Query("select t from Transaction t " +
            "where t.account.accountId = :accountId and " +
            "t.transactionDate =:date")
    List<Transaction> findByAccountIdAndTransactionDate(@Param("accountId") Long accountId,
                                                        @Param("date") Date date);

    @Query("select t from Transaction t " +
            "where t.account.accountId = :accountId " +
            "and lower(t.note) like lower(concat('%', :note, '%')) " +
            "and t.transactionDate = :date")
    List<Transaction> findTransactionsByAccountIdAndNoteLikeAndTransactionDate(@Param("accountId") Long accountId,
                                                                               @Param("note") String note,
                                                                               @Param("date") Date date);

    @Query("select t from Transaction t where t.account.accountId = :accountId and t.transactionDate <= :date")
    List<Transaction> findByAccountIdAndTransactionDateBefore(@Param("accountId") Long accountId,
                                                              @Param("date") Date date);

    List<Transaction> findByTransactionDateAfter(Date date);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findBySubcategory(Subcategory subcategory);

    List<Transaction> findByTransactionAmountBetween(Double from, Double to);

    List<Transaction> findByTransactionAmount(Double amount);

    @Query("select t from Transaction t where t.transactionAmount < ?1")
    List<Transaction> findByTransactionAmountLessThan(Double amount);

    List<Transaction> findByTransactionAmountGreaterThan(Double amount);

    @Query("select count(t) from Transaction t where t.account = :account")
    Long countTransactionByAccount(@Param("account") Account account);
}
