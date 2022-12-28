package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;

import java.util.Optional;


public interface AccountService {
    boolean addAccount(Account account);

    Optional<Account> findByAccountId(Long accountId);

    void deleteAccount(Long id);

    void updateAccount(Long id, String accountName, Double amount);
}
