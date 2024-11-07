package com.youcode.bankify.service;

import com.youcode.bankify.dto.InvoiceRequestDTO;
import com.youcode.bankify.dto.InvoiceResponseDTO;
import com.youcode.bankify.entity.Invoice;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.InvoiceRepository;
import com.youcode.bankify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    public Invoice createInvoice(InvoiceRequestDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setDescription(request.getDescription());
        invoice.setAmount(request.getAmount());
        invoice.setStatus(request.getStatus());
        invoice.setDueDate(request.getDueDate());
        invoice.setUser(user);

        return invoiceRepository.save(invoice);
    }

    public List<InvoiceResponseDTO> getInvoices(Long userId) {
        List<Invoice> invoices = invoiceRepository.findByUserId(userId);
        return invoices.stream().map(invoice -> {
            InvoiceResponseDTO dto = new InvoiceResponseDTO();
            dto.setId(invoice.getId());
            dto.setInvoiceNumber(invoice.getInvoiceNumber());
            dto.setDescription(invoice.getDescription());
            dto.setAmount(invoice.getAmount());
            dto.setStatus(invoice.getStatus());
            dto.setDueDate(invoice.getDueDate());
            return dto;
        }).collect(Collectors.toList());
    }

    public Invoice updateInvoiceStatus(Long invoiceId, String status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }


}
