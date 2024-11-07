package com.youcode.bankify.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanRequestDTO {

    private BigDecimal amount;
    private Integer termInMonths;
    private BigDecimal monthlyIncome;
    private String purpose;

}
