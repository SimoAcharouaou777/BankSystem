package com.youcode.bankify.repository.elasticsearch;

import com.youcode.bankify.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionSearchRepositoryCustom {
    List<Transaction> searchByCriteria(BigDecimal amount, String type , String status, LocalDateTime startDate , LocalDateTime endDate);
}
