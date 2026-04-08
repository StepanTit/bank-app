package com.example.bankapp.service.impl;

import com.example.bankapp.exception.UserAlreadyExistsException;
import com.example.bankapp.exception.UserNotFoundException;
import com.example.bankapp.model.Account;
import com.example.bankapp.model.User;
import com.example.bankapp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final AtomicLong userIdSequence = new AtomicLong(0);

    @Override
    public User createUser(String login, Account initialAccount) {
        if (findUserByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException(login);
        }
        long userId = userIdSequence.incrementAndGet();
        User user = new User(userId, login);
        if (initialAccount != null) {
            user.addAccount(initialAccount);
        }
        usersById.put(userId, user);
        return user;
    }

    @Override
    public User findById(long id) {
        User user = usersById.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    @Override
    public Optional<User> findByIdOptional(long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(usersById.values());
    }

    private Optional<User> findUserByLogin(String login) {
        String key = Objects.requireNonNull(login, "login");
        return usersById.values().stream()
                .filter(u -> key.equals(u.getLogin()))
                .findFirst();
    }
}
