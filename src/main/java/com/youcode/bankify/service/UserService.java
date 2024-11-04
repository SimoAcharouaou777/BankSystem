package com.youcode.bankify.service;


import com.youcode.bankify.dto.TransferRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.AccountRepository;
import com.youcode.bankify.repository.TransactionRepository;
import com.youcode.bankify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        account.setUser(userRepository.findById(userId).orElseThrow( () -> new RuntimeException("user not found")));
        account.setStatus("ACTIVE");
        return accountRepository.save(account);
    }

    public List<Transaction> getTransactionHistory(Long userId, int page , int size){
        Pageable pageable  = PageRequest.of(page, size);
        return transactionRepository.findByUserId(userId, pageable);
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


}
