package com.youcode.bankify.controller;


import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private void checkEmployeeRole(HttpSession session){
        Set<String> roles = (Set<String>) session.getAttribute("roles");
        if(roles == null ){
            throw new RuntimeException("Access denied  : No roles found");
        }
        if(!roles.contains("EMPLOYEE")){
            throw new RuntimeException("Access denied : you dont have permission");
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> viewCustomerAccount(HttpSession session){
        checkEmployeeRole(session);
        List<BankAccount> accounts = employeeService.getCustomerAccounts();
        return ResponseEntity.ok(accounts);

    }

    @PostMapping("/transactions/{transactionId}/approve")
    public ResponseEntity<String> approveTransaction(@PathVariable Long transactionId, HttpSession session){
        checkEmployeeRole(session);
        try{
            employeeService.approveTransaction(transactionId);
            return ResponseEntity.ok("Transaction approved successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/transactions/{transactionId}/reject")
    public ResponseEntity<String> rejectTransaction(@PathVariable Long transactionId, HttpSession session){
        checkEmployeeRole(session);
        try {
            employeeService.rejectTransaction(transactionId);
            return ResponseEntity.ok("Transaction rejected successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/loans/{loanId}/approve")
    public ResponseEntity<String> approveLoan(@PathVariable Long loanId , HttpSession session){
        checkEmployeeRole(session);
        try {
            employeeService.approveLoan(loanId);
            return ResponseEntity.ok("Loan approved Successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/loans/{loanId}/reject")
    public ResponseEntity<String> rejectLoan(@PathVariable Long loanId , HttpSession session){
        checkEmployeeRole(session);
        try {
            employeeService.rejectLoan(loanId);
            return ResponseEntity.ok("Loan rejected successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/invoices/{invoiceId}/approve")
    public ResponseEntity<String> approveInvoice(@PathVariable Long invoiceId, HttpSession session) {
        checkEmployeeRole(session);
        try {
            employeeService.approveInvoice(invoiceId);
            return ResponseEntity.ok("Invoice approved successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/invoices/{invoiceId}/reject")
    public ResponseEntity<String> rejectInvoice(@PathVariable Long invoiceId, HttpSession session) {
        checkEmployeeRole(session);
        try {
            employeeService.rejectInvoice(invoiceId);
            return ResponseEntity.ok("Invoice rejected successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
