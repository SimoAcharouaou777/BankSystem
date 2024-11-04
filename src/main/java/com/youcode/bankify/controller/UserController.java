package com.youcode.bankify.controller;


import com.youcode.bankify.dto.TransferRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccounts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        List<BankAccount> accounts = userService.getBankAccounts(userId, page,size);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/accounts")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount account, @RequestParam Long userId){
        BankAccount createdAccount = userService.createBankAccount(account, userId);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        List<Transaction> transactions = userService.getTransactionHistory(userId,page,size);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@RequestBody TransferRequest transferRequest){
        userService.transferFunds(transferRequest);
        return ResponseEntity.ok("Transfer successful");
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user){
        User updateUser = userService.updateProfile(user);
        return ResponseEntity.ok(updateUser);
    }
}
