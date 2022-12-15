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

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void deleteTransactions(List<Long> ids) {
        ids.forEach(id -> {
            Optional<Transaction> transaction = transactionRepository.findById(id);
            transaction.ifPresent(t -> transactionRepository.deleteById(t.getTransactionId()));
        });
    }

    @Override
    @Transactional
    public boolean addTransaction(Account account,
                                  Double amount, String note, Date date,
                                  Category category, Subcategory subcategory) {
        if (account == null || amount <= 0) {
            return false;
        }

        Transaction transaction = new Transaction(amount, note, date, account, category, subcategory);
        transactionRepository.save(transaction);
        return true;
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAccountTransactions(Account account) {
        return transactionRepository.findByAccount(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByNotes(String note) {
        return transactionRepository.findByNoteLike(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDate(Date date) {
        return transactionRepository.findByTransactionDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateBefore(Date date) {
        return transactionRepository.findByTransactionDateBefore(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateAfter(Date date) {
        return transactionRepository.findByTransactionDateAfter(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByCategory(Category category) {
        return transactionRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsBySubcategory(Subcategory subcategory) {
        return transactionRepository.findBySubcategory(subcategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmount(Double amount) {
        return transactionRepository.findByTransactionAmount(amount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountGreaterThan(Double amount) {
        return transactionRepository.findByTransactionAmountGreaterThan(amount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountLEssThan(Double amount) {
        return transactionRepository.findByTransactionAmountLessThan(amount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountBetween(Double from, Double to) {
        return transactionRepository.findByTransactionAmountBetween(from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countTransactions(Account account) {
        return transactionRepository.countTransactionByAccount(account);
    }
}
