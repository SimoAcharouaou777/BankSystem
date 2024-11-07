package com.youcode.bankify.controller;


import com.youcode.bankify.dto.TransactionRequest;
import com.youcode.bankify.dto.TransactionResponse;
import com.youcode.bankify.dto.TransferRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.AccountRepository;
import com.youcode.bankify.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccounts(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body(null);
        }

        List<BankAccount> accounts = userService.getBankAccounts(userId, page,size);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/accounts")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount account,HttpSession session ){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body(null);
        }
        try{
            BankAccount createdAccount = userService.createBankAccount(account, userId);
            return ResponseEntity.ok(createdAccount);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body(null);
        }

        List<TransactionResponse> transactions = userService.getTransactionHistory(userId,page,size);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@RequestBody TransferRequest transferRequest, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        if(userId == null){
            return ResponseEntity.status(401).body("Unauthorized");
        }

        BankAccount fromAccount = accountRepository.findById(transferRequest.getFromAccount())
                        .orElseThrow(() -> new RuntimeException("From account not found"));

        if(!fromAccount.getUser().getId().equals(userId)){
            return ResponseEntity.status(403).body("You are not authorized to transfer from this account");
        }

        if("PERMANENT".equalsIgnoreCase(transferRequest.getTransactionType())){
            if(transferRequest.getFrequency() == null || transferRequest.getFrequency().isEmpty()){
                return ResponseEntity.badRequest().body("Frequency is required for permanent transfers");
            }
            userService.schedulePermanentTransfer(transferRequest);
            return ResponseEntity.ok("Permanent transfer scheduled successfully");
        }else{
            userService.transferFunds(transferRequest);
            return ResponseEntity.ok("Transfer successful");
        }



    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user){
        User updateUser = userService.updateProfile(user);
        return ResponseEntity.ok(updateUser);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositMoney(@RequestBody TransactionRequest transactionRequest, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body("unauthorized");
        }

        try{
            userService.depositMoney(userId, transactionRequest.getAccountId(), transactionRequest.getAmount());
            return ResponseEntity.ok("Money deposited successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(@RequestBody TransactionRequest transactionRequest, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body("unauthorized");
        }

        try{
            userService.withdrawMoney(userId, transactionRequest.getAccountId(), transactionRequest.getAmount());
            return ResponseEntity.ok("Money withdrawn successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
