package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("select t from Transaction t " +
            "where t.account.accountId = :accountId " +
            "and (:note is null or (lower(t.note) like lower(concat('%', :note, '%')))) " +
            "and ((:from is null or :to is null) or (t.transactionDate between :from and :to)) " +
            "and (:amount is null or (t.transactionAmount =:amount))")
    List<Transaction> findTransactionsByAccountIdAndNoteLikeAndTransactionDate(@Param("accountId") Long accountId,
                                                                               @Param("note") String note,
                                                                               @Param("from") Date from,
                                                                               @Param("to") Date to,
                                                                               @Param("amount") Double amount);

    @Query("select count(t) from Transaction t where t.account = :account")
    Long countTransactionByAccount(@Param("account") Account account);
}
