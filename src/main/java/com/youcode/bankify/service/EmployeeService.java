package com.youcode.bankify.service;


import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.Invoice;
import com.youcode.bankify.entity.Loan;
import com.youcode.bankify.entity.Transaction;
import com.youcode.bankify.repository.jpa.AccountRepository;
import com.youcode.bankify.repository.jpa.InvoiceRepository;
import com.youcode.bankify.repository.jpa.LoanRepository;
import com.youcode.bankify.repository.jpa.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<BankAccount> getCustomerAccounts(){
        return accountRepository.findAll();
    }

    public void approveTransaction(Long transactionId){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus("APPROVED");
        transactionRepository.save(transaction);
    }

    public void rejectTransaction(Long transactionId){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus("REJECTED");
        transactionRepository.save(transaction);
    }

    public void approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setStatus("APPROVED");
        loanRepository.save(loan);
    }

    public void rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setStatus("REJECTED");
        loanRepository.save(loan);
    }

    public void approveInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus("APPROVED");
        invoiceRepository.save(invoice);
    }

    public void rejectInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus("REJECTED");
        invoiceRepository.save(invoice);
    }



}
