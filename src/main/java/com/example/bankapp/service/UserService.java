package com.example.bankapp.service;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    User createUser(String login, Account initialAccount);

    User findById(long id);

    Optional<User> findByIdOptional(long id);

    Collection<User> findAll();
}
