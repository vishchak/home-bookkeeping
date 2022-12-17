package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CurrentUser;


public interface AccountService {
    boolean addAccount(Account account);

    Account findByAccountName(String name, CurrentUser currentUser);

    void deleteAccount(Long id);

    void updateAccount(Long id, String accountName, Double amount);
}
