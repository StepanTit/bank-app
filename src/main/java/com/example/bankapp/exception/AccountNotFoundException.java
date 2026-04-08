package com.example.bankapp.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long accountId) {
        super("Account with id=" + accountId + " not found");
    }
}

