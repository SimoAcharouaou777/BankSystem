package com.youcode.bankify.dto;

import java.math.BigDecimal;

public class TransactionRequest {

    private Long accountId;
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
