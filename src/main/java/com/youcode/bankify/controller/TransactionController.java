package com.youcode.bankify.controller;

import com.youcode.bankify.dto.TransactionSearchRequest;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/search")
    public ResponseEntity<List<Transaction>> searchTransactions(@RequestBody TransactionSearchRequest searchRequest){
        List<Transaction> result = transactionService.searchTransactions(
                searchRequest.getAmount(),
                searchRequest.getType(),
                searchRequest.getStatus(),
                searchRequest.getStartDate(),
                searchRequest.getEndDate()
        );
        return ResponseEntity.ok(result);
    }
}
