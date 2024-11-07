package com.youcode.bankify.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String description;
    private BigDecimal amount;
    private String status;
    private LocalDate dueDate;
}
