package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.*;
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
            "where t.account.user = :user " +
            "and (:account is null or (t.account = :account)) " +
            "and (:note is null or (lower(t.note) like lower(concat('%', :note, '%')))) " +
            "and ((:from is null or :to is null) or (t.transactionDate between :from and :to)) " +
            "and (:amount is null or (t.transactionAmount =:amount))" +
            "and (:category is null or (t.category =:category))" +
            "and (:subcategory is null or (t.subcategory =:subcategory))")
    List<Transaction> findTransactionsByAccountUser(@Param("user") CustomUser user,
                                                    @Param("account") Account account,
                                                    @Param("note") String note,
                                                    @Param("from") Date from,
                                                    @Param("to") Date to,
                                                    @Param("amount") Double amount,
                                                    @Param("category") Category category,
                                                    @Param("subcategory") Subcategory subcategory,
                                                    Pageable pageable);

    @Query("select count(t) from Transaction t where t.account.user = :user " +
            "and (:account is null or (t.account = :account)) " +
            "and (:category is null or (t.category = :category))")
    Long countTransaction(@Param("user") CustomUser user,
                          @Param("account") Account account,
                          @Param("category") Category category);

    @Query("select t from Transaction t where t.account = ?1")
    List<Transaction> findAllTransactionsByAccount(Account account,
                                                   Pageable pageable);

    @Query("select t from Transaction t where t.account.user = :user " +
            "and t.transactionDate between :from and :to " +
            "and (:category is null) or (t.category = :category)")
    List<Transaction> findChartTransactions(CustomUser user, Date from, Date to, Category category);

    @Query("select t from Transaction t where t.account.user = ?1")
    List<Transaction> findTransactionsByAccountUser(CustomUser user,
                                                    Pageable pageable);
}
