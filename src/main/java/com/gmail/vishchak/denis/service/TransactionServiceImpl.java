package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    public List<Transaction> findSpecificUserTransactions(CustomUser user, Account account, String note, Date from, Date to, Double amount, Category category, Subcategory subcategory, int currentPageNUmber, int itemsPerPage) {
        List<Transaction> transactionsByAccountUser = transactionRepository.findTransactionsByAccountUser(user, account, note, from, to, amount, category, subcategory, PageRequest.of(currentPageNUmber, itemsPerPage));
        transactionsByAccountUser.sort(Comparator.comparing(Transaction::getTransactionId).reversed());

        return transactionsByAccountUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPageCount(CustomUser user, Account account, int itemsPerPage) {
        Long totalItems = transactionRepository.countTransaction(user, account, null);

        return totalItems % itemsPerPage == 0 ? totalItems / itemsPerPage : totalItems / itemsPerPage + 1;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTransactionCountByCategory(CustomUser user, Category category) {
        return transactionRepository.countTransaction(user, null, category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findChartTransactions(CustomUser user, Date from, Date to, Category category) {
        return transactionRepository.findChartTransactions(user, from, to, category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllUSerTransactions(CustomUser user, int currentPageNUmber, int itemsPerPage) {
        List<Transaction> transactionsByAccountUser = transactionRepository.findTransactionsByAccountUser(user, PageRequest.of(currentPageNUmber, itemsPerPage));
        transactionsByAccountUser.sort(Comparator.comparing(Transaction::getTransactionId).reversed());

        return transactionsByAccountUser;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllAccountTransactions(Account account, int currentPageNUmber, int itemsPerPage) {
        List<Transaction> allTransactionsByAccount = transactionRepository.findAllTransactionsByAccount(account, PageRequest.of(currentPageNUmber, itemsPerPage));
        allTransactionsByAccount.sort(Comparator.comparing(Transaction::getTransactionId).reversed());

        return allTransactionsByAccount;
    }

    private boolean ifExpense(Category category, Subcategory subcategory) {
        return (category.getCategoryName().equalsIgnoreCase("expense") ||
                (subcategory.getSubcategoryName().equalsIgnoreCase("Repayment") ||
                        subcategory.getSubcategoryName().equalsIgnoreCase("Debt") ||
                        subcategory.getSubcategoryName().equalsIgnoreCase("Goal")));
    }
}
