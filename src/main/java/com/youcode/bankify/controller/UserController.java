package com.youcode.bankify.controller;


import com.youcode.bankify.dto.*;
import com.youcode.bankify.entity.*;
import com.youcode.bankify.repository.AccountRepository;
import com.youcode.bankify.service.InvoiceService;
import com.youcode.bankify.service.LoanService;
import com.youcode.bankify.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private LoanService loanService;

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
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody AccountCreationDTO accountCreationDTO,HttpSession session ){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body(null);
        }
        Set<String> roles = (Set<String>) session.getAttribute("roles");
        if(roles == null || !roles.contains("USER")){
            return ResponseEntity.status(403).body(null);
        }
        try{
            BankAccount createdAccount = userService.createBankAccount(accountCreationDTO, userId);
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
    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceRequestDTO invoiceRequest, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body(null);
        }
        Invoice invoice = invoiceService.createInvoice(invoiceRequest,userId);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoices(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(401).body(null);
        }
        List<InvoiceResponseDTO> invoices = invoiceService.getInvoices(userId);
            return ResponseEntity.ok(invoices);

    }

    @PutMapping("/invoices/{id}/status")
    public ResponseEntity<Invoice> updateInvoiceStatus(@PathVariable Long id , @RequestParam String status){
        Invoice invoice = invoiceService.updateInvoiceStatus(id,status);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/loans")
    public ResponseEntity<Loan> applyForLoan(@RequestBody LoanRequestDTO loanRequest, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        Loan loan = loanService.applyForLoan(loanRequest, userId);
        return ResponseEntity.ok(loan);
    }
    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponseDTO>> getLoans(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        List<LoanResponseDTO> loans = loanService.getLoans(userId);
        return ResponseEntity.ok(loans);
    }
    @PutMapping("/loans/{id}/status")
    public ResponseEntity<Loan> approveOrRejectLoan(@PathVariable Long id, @RequestParam String status) {
        Loan loan = loanService.approveOrRejectLoan(id, status);
        return ResponseEntity.ok(loan);
    }

}
