package com.youcode.bankify.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private BigDecimal amount;
    private String type;
    private String otherPartyUsername;
    private LocalDateTime date;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOtherPartyUsername() {
        return otherPartyUsername;
    }

    public void setOtherPartyUsername(String otherPartyUsername) {
        this.otherPartyUsername = otherPartyUsername;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
