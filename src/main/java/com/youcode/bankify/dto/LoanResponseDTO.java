package com.youcode.bankify.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanResponseDTO {

    private Long id;
    private BigDecimal amount;
    private Integer termInMonths;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime applicationDate;
}
