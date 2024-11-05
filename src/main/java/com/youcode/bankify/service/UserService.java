package com.youcode.bankify.service;


import com.youcode.bankify.dto.TransactionResponse;
import com.youcode.bankify.dto.TransferRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.ScheduledTransfer;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.AccountRepository;
import com.youcode.bankify.repository.ScheduledTransferRepository;
import com.youcode.bankify.repository.TransactionRepository;
import com.youcode.bankify.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
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
    @Autowired
    private ScheduledTransferRepository scheduledTransferRepository;

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

    public void transferFunds(TransferRequest transferRequest) {
        BigDecimal transactionFee = calculateTransactionFee(transferRequest);

        BigDecimal totalDebitAmount = BigDecimal.valueOf(transferRequest.getAmount()).add(transactionFee);
        BankAccount fromAccount = getAccountWithBalanceCheck(transferRequest.getFromAccount(), totalDebitAmount);
        BankAccount toAccount = accountRepository.findById(transferRequest.getToAccount())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        // Process the actual transfer and create transactions
        processTransfer(fromAccount, toAccount, BigDecimal.valueOf(transferRequest.getAmount()), transactionFee);
    }



    public void schedulePermanentTransfer(TransferRequest transferRequest) {
        ScheduledTransfer scheduledTransfer = new ScheduledTransfer();
        scheduledTransfer.setFromAccountId(transferRequest.getFromAccount());
        scheduledTransfer.setToAccountId(transferRequest.getToAccount());
        scheduledTransfer.setAmount(BigDecimal.valueOf(transferRequest.getAmount()));
        scheduledTransfer.setFrequency(transferRequest.getFrequency().toUpperCase());
        LocalDateTime nextExecutionDate = calculateInitialExecutionDate(transferRequest.getFrequency());
        scheduledTransfer.setNexExecutionDate(nextExecutionDate);

        scheduledTransferRepository.save(scheduledTransfer);
    }

    private void processTransfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal transferAmount, BigDecimal transactionFee) {
        // Deduct from sender's account (including fees)
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount.add(transactionFee)));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record transactions for both debit and credit
        recordTransaction(fromAccount, transferAmount, "DEBIT");
        recordTransaction(toAccount, transferAmount, "CREDIT");
    }

    private void recordTransaction(BankAccount account, BigDecimal amount, String type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDate(LocalDateTime.now());
        transaction.setBankAccount(account);
        transaction.setUser(account.getUser());
        transactionRepository.save(transaction);
    }


    private BigDecimal calculateTransactionFee(TransferRequest transferRequest) {
        BigDecimal transferAmount = BigDecimal.valueOf(transferRequest.getAmount());
        switch (transferRequest.getTransactionType().toUpperCase()) {
            case "CLASSIC":
                return transferAmount.multiply(BigDecimal.valueOf(0.01));
            case "INSTANT":
                return transferAmount.multiply(BigDecimal.valueOf(0.02));
            case "PERMANENT":
                schedulePermanentTransfer(transferRequest);
                return BigDecimal.ZERO; // No immediate fee as it's a scheduled transfer
            default:
                throw new RuntimeException("Invalid transaction type");
        }
    }

    private BankAccount getAccountWithBalanceCheck(Long accountId, BigDecimal amountToCheck) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amountToCheck) < 0) {
            throw new RuntimeException("Insufficient funds, including transaction fee");
        }
        return account;
    }

    private LocalDateTime calculateInitialExecutionDate(String frequency) {
        LocalDateTime currentDate = LocalDateTime.now();
        switch (frequency.toUpperCase()) {
            case "WEEKLY":
                return currentDate.plusWeeks(1);
            case "MONTHLY":
                return currentDate.plusMonths(1);
            case "YEARLY":
                return currentDate.plusYears(1);
            default:
                throw new IllegalArgumentException("Invalid frequency type: " + frequency);
        }
    }


    public User updateProfile(User user){
        return userRepository.save(user);
    }

    private String generatedAccountNumber(){
        return RandomStringUtils.randomNumeric(12);
    }


}
