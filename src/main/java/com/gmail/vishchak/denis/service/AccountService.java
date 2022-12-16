package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CurrentUser;


public interface AccountService {
    boolean addAccount(Account account);

    Account findByAccountName(String name, CurrentUser currentUser);

//    void deleteAccount(Account account);

//    void updateAccount(Account account);
}
