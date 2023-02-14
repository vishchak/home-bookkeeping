package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;

import java.util.List;
import java.util.Optional;


public interface AccountService {
    void addAccount(Account account);

    Optional<Account> findByAccountId(Long accountId);

    void deleteAccount(Long id);

    void updateAccount(Long id, String accountName, Double amount);

    List<Account> findAccountsByUser(CustomUser user);
}
