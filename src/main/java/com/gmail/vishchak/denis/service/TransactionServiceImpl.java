package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
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
    public void addTransaction(Transaction transaction) {
        if (transaction.getTransactionAmount() <= 0) {
            return;
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
    public List<Transaction> findAccountTransactions(Account account, String note, Date from, Date to, Double amount, Category category, Subcategory subcategory,
                                                     int currentPageNUmber, int itemsPerPage) {
        return transactionRepository.findTransactionsByAccount(account.getAccountId(), note, from, to, amount, category, subcategory,
                PageRequest.of(currentPageNUmber, itemsPerPage));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPageCount(Account account, int itemsPerPage) {
        Long totalItems = transactionRepository.countTransactionByAccount(account);

        return totalItems % itemsPerPage == 0 ? totalItems / itemsPerPage : totalItems / itemsPerPage + 1;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllTransactionByAccount(Account account,
                                                         int currentPageNUmber, int itemsPerPage) {
        return transactionRepository.findAllTransactionsByAccount(account,
                PageRequest.of(currentPageNUmber, itemsPerPage));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findChartTransactions(Account account, Date from, Date to, Category category) {
        return transactionRepository.findChartTransactions(account, from, to, category);
    }

    private boolean ifExpense(Category category, Subcategory subcategory) {
        return (category.getCategoryName().equalsIgnoreCase("expense") ||
                (subcategory.getSubcategoryName().equalsIgnoreCase("Repayment") ||
                        subcategory.getSubcategoryName().equalsIgnoreCase("Debt") ||
                        subcategory.getSubcategoryName().equalsIgnoreCase("Goal")));
    }
}
