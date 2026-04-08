package com.example.bankapp.service.impl;

import com.example.bankapp.exception.AccountNotFoundException;
import com.example.bankapp.exception.CannotCloseLastAccountException;
import com.example.bankapp.exception.InsufficientFundsException;
import com.example.bankapp.exception.InvalidAmountException;
import com.example.bankapp.model.Account;
import com.example.bankapp.model.User;
import com.example.bankapp.service.AccountService;
import com.example.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccountServiceImpl implements AccountService {

    private final Map<Long, Account> accountsById = new ConcurrentHashMap<>();
    private final AtomicLong accountIdSequence = new AtomicLong(0);

    private final UserService userService;

    private final BigDecimal defaultAmount;
    private final BigDecimal transferCommissionPercent;

    public AccountServiceImpl(UserService userService,
                              @Value("${account.default-amount}") BigDecimal defaultAmount,
                              @Value("${account.transfer-commission}") BigDecimal transferCommissionPercent) {
        this.userService = userService;
        this.defaultAmount = defaultAmount;
        this.transferCommissionPercent = transferCommissionPercent;
    }

    @Override
    public Account createAccount(long userId) {
        return createAccountInternal(userId, defaultAmount);
    }

    private Account createAccountInternal(long userId, BigDecimal initialAmount) {
        User user = userService.findById(userId);
        long accountId = accountIdSequence.incrementAndGet();
        Account account = new Account(accountId, user.getId(), initialAmount);
        accountsById.put(accountId, account);
        user.addAccount(account);
        return account;
    }

    @Override
    public void deposit(long accountId, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = getAccount(accountId);
        account.setMoneyAmount(account.getMoneyAmount().add(amount));
    }

    @Override
    public void withdraw(long accountId, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = getAccount(accountId);
        BigDecimal current = account.getMoneyAmount();
        if (current.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId, current, amount);
        }
        account.setMoneyAmount(current.subtract(amount));
    }

    @Override
    public void transfer(long sourceAccountId, long targetAccountId, BigDecimal amount) {
        validatePositiveAmount(amount);
        if (sourceAccountId == targetAccountId) {
            throw new IllegalArgumentException("Source and target account IDs must be different");
        }

        Account source = getAccount(sourceAccountId);
        Account target = getAccount(targetAccountId);

        boolean sameUser = source.getUserId() == target.getUserId();

        if (sameUser) {
            withdraw(sourceAccountId, amount);
            deposit(targetAccountId, amount);
            return;
        }
        BigDecimal commission = amount
                .multiply(transferCommissionPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalToWithdraw = amount.add(commission);
        BigDecimal current = source.getMoneyAmount();
        if (current.compareTo(totalToWithdraw) < 0) {
            throw new InsufficientFundsException(sourceAccountId, current, totalToWithdraw);
        }
        source.setMoneyAmount(current.subtract(totalToWithdraw));
        target.setMoneyAmount(target.getMoneyAmount().add(amount));
    }

    @Override
    public void closeAccount(long accountId) {
        Account account = getAccount(accountId);
        User user = userService.findById(account.getUserId());
        if (user.getAccountList().size() <= 1) {
            throw new CannotCloseLastAccountException(user.getId(), accountId);
        }
        Account firstAccount = user.getAccountList().get(0);
        if (firstAccount.getId() == accountId) {
            firstAccount = user.getAccountList().get(1);
        }

        BigDecimal balanceToTransfer = account.getMoneyAmount();
        if (balanceToTransfer.compareTo(BigDecimal.ZERO) > 0) {
            firstAccount.setMoneyAmount(firstAccount.getMoneyAmount().add(balanceToTransfer));
        }

        user.removeAccount(account);
        accountsById.remove(accountId);
    }

    @Override
    public Account getAccount(long accountId) {
        Account account = accountsById.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return account;
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }
}
