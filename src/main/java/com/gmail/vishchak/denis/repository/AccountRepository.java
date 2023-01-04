package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CurrentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.user= ?1")
    List<Account> findByUser(CurrentUser user);

    boolean existsByUserAndAccountName(CurrentUser user, String name);
}
