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
    public List<Transaction> findAccountTransactions(Account account, String note, Date date) {
        boolean filterByNote = !(note == null || note.isEmpty() || note.equals("Filter by note"));
        boolean filterByDate = date != null;

        if (filterByNote && filterByDate) {
            return transactionRepository.findTransactionsByAccountIdAndNoteLikeAndTransactionDate(account.getAccountId(), note, date);
        }

        if (filterByNote) {
            return transactionRepository.findByAccountIdAndNoteLike(account.getAccountId(), note);
        }
        if (filterByDate) {
            return transactionRepository.findByAccountIdAndTransactionDate(account.getAccountId(), date);
        }
        return transactionRepository.findByAccount(account.getAccountId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAccountTransactionsByDateBefore(Account account, Date date) {
        return transactionRepository.findByAccountIdAndTransactionDateBefore(account.getAccountId(), date);
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
    public List<Transaction> findTransactionsByAmount(Double from, Double to) {
        if (to == null || to < from) {
            return transactionRepository.findByTransactionAmount(from);
        } else {
            return transactionRepository.findByTransactionAmountBetween(from, to);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountGreaterThan(Double amount) {
        return transactionRepository.findByTransactionAmountGreaterThan(amount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByAmountLessThan(Double amount) {
        return transactionRepository.findByTransactionAmountLessThan(amount);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countTransactions(Account account) {
        return transactionRepository.countTransactionByAccount(account);
    }
}
