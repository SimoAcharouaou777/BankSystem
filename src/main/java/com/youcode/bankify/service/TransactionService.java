package com.youcode.bankify.service;

import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.repository.elasticsearch.TransactionSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionSearchRepository transactionSearchRepository;

    public List<Transaction> searchTransactions(BigDecimal amount, String type, String status, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionSearchRepository.searchByCriteria(amount, type, status, startDate, endDate);
    }
}
