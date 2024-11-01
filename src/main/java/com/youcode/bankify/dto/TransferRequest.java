package com.youcode.bankify.dto;

public class TransferRequest {

    private Long fromAccount;
    private Long toAccount;
    private double amount;

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public double getAmount() {
        return amount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }

}
