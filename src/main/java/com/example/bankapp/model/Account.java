package com.example.bankapp.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private final long id;
    private final long userId;
    private BigDecimal moneyAmount;

    public Account(long id, long userId, BigDecimal moneyAmount) {
        this.id = id;
        this.userId = userId;
        this.moneyAmount = Objects.requireNonNull(moneyAmount, "moneyAmount must not be null");
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public BigDecimal getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = Objects.requireNonNull(moneyAmount, "moneyAmount must not be null");
    }

    @Override
    public String toString() {
        return "Account{id=" + id +
                ", userId=" + userId +
                ", moneyAmount=" + moneyAmount +
                '}';
    }
}

