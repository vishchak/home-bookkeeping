package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;

import java.util.List;


public interface AccountService {
    void addAccount(Account account);

    void updateAccount(Long id, String accountName, Double amount);

    List<Account> findAccountsByUser(CustomUser user);
}
