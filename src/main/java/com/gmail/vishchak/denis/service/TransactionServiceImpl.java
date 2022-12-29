package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountServiceImpl accountService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountServiceImpl accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        transaction.ifPresent(t -> transactionRepository.deleteById(t.getTransactionId()));
    }

    @Override
    @Transactional
    public boolean addTransaction(Transaction transaction) {
        if (transaction.getAccount() == null || transaction.getTransactionAmount() <= 0) {
            return false;
        }

        if (transaction.getNote().isEmpty() || transaction.getNote() == null) {
            transaction.setNote("-");
        }

        transactionRepository.save(transaction);

        Double sum = transaction.getTransactionAmount();
        if (ifExpense(transaction.getCategory(), transaction.getSubcategory())) {
            sum = sum * (-1);
        }

        accountService.updateAccount(transaction.getAccount().getAccountId(), null, transaction.getAccount().getAccountAmount() + sum);
        return true;
    }

    @Override
    @Transactional
    public void updateTransaction(Long id,
                                  Double amount, String note,
                                  Category category, Subcategory subcategory) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        transaction.ifPresent(t -> {

            Double oldSum = t.getTransactionAmount();
            if (!ifExpense(t.getCategory(), t.getSubcategory())) {
                oldSum *= (-1);
            }

            Double newSum = amount;
            if (ifExpense(category, subcategory)) {
                newSum = amount * (-1);
            }

            t.setTransactionAmount(amount);
            t.setNote(note);
            t.setCategory(category);
            t.setSubcategory(subcategory);
            transactionRepository.save(t);

            accountService.updateAccount(t.getAccount().getAccountId(), null, t.getAccount().getAccountAmount() + oldSum + newSum);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAccountTransactions(Account account, String note, Date from, Date to, Double amount, String category, String subcategory) {
        return transactionRepository.findTransactionsByAccount(account.getAccountId(), note, from, to, amount, category, subcategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countTransactions(Account account) {
        return transactionRepository.countTransactionByAccount(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByCategory(Account account, Long categoryId) {
        return transactionRepository.findTransactionsByAccountAndCategoryId(account.getAccountId(), categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllTransactionByAccount(Account account) {
        return transactionRepository.findAllTransactionsByAccount(account);
    }

    private boolean ifExpense(Category category, Subcategory subcategory) {
        return (category.getCategoryName().equalsIgnoreCase("expense") ||
                (subcategory.getSubcategoryName().equalsIgnoreCase("Repayment") ||
                        subcategory.getSubcategoryName().equalsIgnoreCase("Debt") ||
                        subcategory.getSubcategoryName().equalsIgnoreCase("Goal")));
    }
}
