package com.example.bankapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {

    private final long id;
    private final String login;
    private final List<Account> accountList = new ArrayList<>();

    public User(long id, String login) {
        this.id = id;
        this.login = Objects.requireNonNull(login, "login must not be null");
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return Collections.unmodifiableList(accountList);
    }

    public void addAccount(Account account) {
        accountList.add(Objects.requireNonNull(account));
    }

    public void removeAccount(Account account) {
        accountList.remove(account);
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", login='" + login + '\'' +
                ", accountList=" + accountList +
                '}';
    }
}

