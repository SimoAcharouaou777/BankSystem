package com.youcode.bankify.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionSearchRequest {
    private BigDecimal amount;
    private String type;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
