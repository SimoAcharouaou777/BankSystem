package com.youcode.bankify.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceRequestDTO {

    private String invoiceNumber;
    private String description;
    private BigDecimal amount;
    private String status; // PENDING, PAID
    private LocalDate dueDate;
}
