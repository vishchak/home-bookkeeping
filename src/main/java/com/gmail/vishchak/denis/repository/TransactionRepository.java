package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
    @Query("select t from Transaction t " +
            "where t.account.accountId = :accountId " +
            "and (:note is null or (lower(t.note) like lower(concat('%', :note, '%')))) " +
            "and ((:from is null or :to is null) or (t.transactionDate between :from and :to)) " +
            "and (:amount is null or (t.transactionAmount =:amount))" +
            "and (:category is null or (t.category =:category))" +
            "and (:subcategory is null or (t.subcategory =:subcategory))")
    List<Transaction> findTransactionsByAccount(@Param("accountId") Long accountId,
                                                @Param("note") String note,
                                                @Param("from") Date from,
                                                @Param("to") Date to,
                                                @Param("amount") Double amount,
                                                @Param("category") Category category,
                                                @Param("subcategory") Subcategory subcategory,
                                                Pageable pageable);

    @Query("select count(t) from Transaction t where t.account = :account")
    Long countTransactionByAccount(@Param("account") Account account);

    @Query("select t from Transaction t where t.account = ?1")
    List<Transaction> findAllTransactionsByAccount(Account account,
                                                   Pageable pageable);

    @Query("select t from Transaction t where t.account = :account " +
            "and t.transactionDate between :from and :to " +
            "and (:category is null) or (t.category = :category)")
    List<Transaction> findChartTransactions(Account account, Date from, Date to, Category category);
}
