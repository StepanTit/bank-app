package com.example.bankapp.service;

import com.example.bankapp.model.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account createAccount(long userId);

    void deposit(long accountId, BigDecimal amount);

    void withdraw(long accountId, BigDecimal amount);

    void transfer(long sourceAccountId, long targetAccountId, BigDecimal amount);

    void closeAccount(long accountId);

    Account getAccount(long accountId);
}
