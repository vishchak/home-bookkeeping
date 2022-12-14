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
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void deleteTransactions(List<Long> ids) {
        ids.forEach(id -> {
            Optional<Transaction> transaction = transactionRepository.findById(id);
            transaction.ifPresent(t -> transactionRepository.deleteById(t.getTransactionId()));
        });
    }

    @Transactional
    public boolean addTransaction(Account account,
                                  Double amount, String note, Date date,
                                  Category category, Subcategory subcategory) {
        if (account == null || amount <= 0) {
            return false;
        }

        Transaction transaction = new Transaction(amount, note, date, account, category, subcategory);
        return true;
    }

    @Transactional
    public void updateTransaction(Long id,
                                  Double amount, String note,
                                  Category category, Subcategory subcategory) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        transaction.ifPresent(t -> {
            t.setTransactionAmount(amount);
            t.setNote(note);
            t.setCategory(category);
            t.setSubcategory(subcategory);
            transactionRepository.save(t);
        });
    }

    @Transactional(readOnly = true)

    public List<Transaction> findAccountTransactions(Account account) {
        return transactionRepository.findByAccount(account);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByNotes(String note) {
        return transactionRepository.findByNoteLike(note);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDate(Date date) {
        return transactionRepository.findByTransactionDate(date);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateBefore(Date date) {
        return transactionRepository.findByTransactionDateBefore(date);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateAfter(Date date) {
        return transactionRepository.findByTransactionDateAfter(date);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByCategory(Category category) {
        return transactionRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsBySubcategory(Subcategory subcategory) {
        return transactionRepository.findBySubcategory(subcategory);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmount(Double amount) {
        return transactionRepository.findByTransactionAmount(amount);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountGreaterThan(Double amount) {
        return transactionRepository.findByTransactionAmountGreaterThan(amount);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountLEssThan(Double amount) {
        return transactionRepository.findByTransactionAmountLessThan(amount);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountBetween(Double from, Double to) {
        return transactionRepository.findByTransactionAmountBetween(from, to);
    }

    @Transactional(readOnly = true)
    public Long countTransactions(Account account) {
        return transactionRepository.countTransactionByAccount(account);
    }
}
