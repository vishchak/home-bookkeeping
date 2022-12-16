package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public boolean addAccount(Account account) {
        if (account == null) {
            return false;
        }
        accountRepository.save(account);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Account findByAccountName(String name, CurrentUser currentUser) {
        List<Account> accountList = accountRepository.findByUser(currentUser);
        for (Account a :
                accountList) {
            if (a.getAccountName().equalsIgnoreCase(name))
                return a;
        }
        throw new RuntimeException();
    }
}
