package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public boolean addAccount(Account account) {
        if (account == null || accountRepository.existsByUserAndAccountName(account.getUser(), account.getAccountName())) {
            return false;
        }
        accountRepository.save(account);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByAccountId(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        account.ifPresent(a -> accountRepository.deleteById(a.getAccountId()));
    }

    @Override
    @Transactional
    public void updateAccount(Long id, String accountName, Double amount) {
        Optional<Account> account = accountRepository.findById(id);
        account.ifPresent(c -> {
            c.setAccountAmount(amount);
            if (accountName == null || accountName.isEmpty()) {
                accountRepository.save(c);
                return;
            }
            c.setAccountName(accountName);
            accountRepository.save(c);
        });
    }
}
