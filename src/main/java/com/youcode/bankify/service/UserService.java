package com.youcode.bankify.service;


import com.youcode.bankify.dto.TransactionResponse;
import com.youcode.bankify.dto.TransferRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.AccountRepository;
import com.youcode.bankify.repository.TransactionRepository;
import com.youcode.bankify.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    public List<BankAccount> getBankAccounts(Long userId, int page , int size){
        Pageable pageable = PageRequest.of(page,size);
        return accountRepository.findByUserId(userId, pageable).getContent();
    }

    public BankAccount createBankAccount(BankAccount account, Long userId){
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        account.setUser(user);
        account.setStatus("ACTIVE");
        String accountNumber;
        do{
            accountNumber = generatedAccountNumber();
        }while(accountRepository.existsByAccountNumber(accountNumber));

        account.setAccountNumber(accountNumber);
        if(account.getBalance() == null || account.getBalance().compareTo(BigDecimal.valueOf(100)) < 0){
            throw new RuntimeException("Initial balance must be at least 100.00");
        }

        return accountRepository.save(account);
    }

    public List<TransactionResponse> getTransactionHistory(Long userId, int page , int size){
        Pageable pageable  = PageRequest.of(page, size);
        List<Transaction> transactions = transactionRepository.findByUserId(userId,pageable);
        List<TransactionResponse> transactionResponses = transactions.stream().map(transaction -> {
            TransactionResponse dto = new TransactionResponse();
            dto.setAmount(transaction.getAmount());
            dto.setDate(transaction.getDate());

            if(transaction.getBankAccount().getUser().getId().equals(userId)){
                dto.setType("SENT");
                dto.setOtherPartyUsername(transaction.getUser().getUsername());
            }else{
                dto.setType("RECEIVED");
                dto.setOtherPartyUsername(transaction.getBankAccount().getUser().getUsername());
            }
            return dto;
        }).collect(Collectors.toList());
        return transactionResponses;
    }

    public void transferFunds(TransferRequest transferRequest){
        BankAccount fromAccount = accountRepository.findById(transferRequest.getFromAccount())
                .orElseThrow(() -> new RuntimeException("From account not found"));
        BankAccount toAccount = accountRepository.findById(transferRequest.getToAccount())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        BigDecimal transferAmount = BigDecimal.valueOf(transferRequest.getAmount());

        if(fromAccount.getBalance().compareTo(BigDecimal.valueOf(transferRequest.getAmount())) < 0 ){
            throw  new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        LocalDateTime transactionDate = LocalDateTime.now();

        Transaction debitTransaction = new Transaction();
        debitTransaction.setAmount(transferAmount);
        debitTransaction.setType("DEBIT");
        debitTransaction.setDate(transactionDate);
        debitTransaction.setBankAccount(fromAccount);
        debitTransaction.setUser(fromAccount.getUser());

        Transaction creditTransaction = new Transaction();
        creditTransaction.setAmount(transferAmount);
        creditTransaction.setType("CREDIT");
        creditTransaction.setDate(transactionDate);
        creditTransaction.setBankAccount(toAccount);
        creditTransaction.setUser(toAccount.getUser());

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);


    }

    public User updateProfile(User user){
        return userRepository.save(user);
    }

    private String generatedAccountNumber(){
        return RandomStringUtils.randomNumeric(12);
    }


}
