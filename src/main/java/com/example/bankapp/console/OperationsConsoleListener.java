package com.example.bankapp.console;

import com.example.bankapp.exception.CannotCloseLastAccountException;
import com.example.bankapp.exception.InsufficientFundsException;
import com.example.bankapp.exception.InvalidAmountException;
import com.example.bankapp.exception.UserAlreadyExistsException;
import com.example.bankapp.exception.AccountNotFoundException;
import com.example.bankapp.exception.UserNotFoundException;
import com.example.bankapp.model.Account;
import com.example.bankapp.model.User;
import com.example.bankapp.service.AccountService;
import com.example.bankapp.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Scanner;

@Component
public class OperationsConsoleListener {

    private final UserService userService;
    private final AccountService accountService;

    public OperationsConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String operationInput = scanner.nextLine().trim();
            if (operationInput.isEmpty()) {
                continue;
            }

            OperationType operationType;
            try {
                operationType = OperationType.fromString(operationInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown command: " + operationInput);
                continue;
            }

            try {
                switch (operationType) {
                    case USER_CREATE -> handleUserCreate(scanner);
                    case SHOW_ALL_USERS -> handleShowAllUsers();
                    case ACCOUNT_CREATE -> handleAccountCreate(scanner);
                    case ACCOUNT_CLOSE -> handleAccountClose(scanner);
                    case ACCOUNT_DEPOSIT -> handleAccountDeposit(scanner);
                    case ACCOUNT_TRANSFER -> handleAccountTransfer(scanner);
                    case ACCOUNT_WITHDRAW -> handleAccountWithdraw(scanner);
                    default -> System.out.println("Unsupported operation: " + operationType);
                }
            } catch (Exception e) {
                System.out.println("Error executing command " + operationType + ": error=" + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("Please enter one of operation type:");
        System.out.println("-ACCOUNT_CREATE");
        System.out.println("-SHOW_ALL_USERS");
        System.out.println("-ACCOUNT_CLOSE");
        System.out.println("-ACCOUNT_WITHDRAW");
        System.out.println("-ACCOUNT_DEPOSIT");
        System.out.println("-ACCOUNT_TRANSFER");
        System.out.println("-USER_CREATE");
    }

    private void handleUserCreate(Scanner scanner) {
        System.out.println("Enter login for new user:");
        String login = scanner.nextLine().trim();
        try {
            User user = userService.createUser(login, null);

            Account userAccount = accountService.createAccount(user.getId());

            System.out.println("User created: " + user);
        } catch (UserAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleShowAllUsers() {
        Collection<User> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.println("List of all users:");
        for (User user : users) {
            System.out.println(user);
        }
    }

    private void handleAccountCreate(Scanner scanner) {
        System.out.println("Enter the user id for which to create an account:");
        long userId = readLong(scanner);
        try {
            User user = userService.findById(userId);
            Account account = accountService.createAccount(user.getId());
            System.out.println("New account created with ID: " + account.getId() + " for user: " + user.getLogin());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleAccountClose(Scanner scanner) {
        System.out.println("Enter account ID to close:");
        long accountId = readLong(scanner);
        try {
            accountService.closeAccount(accountId);
            System.out.println("Account with ID " + accountId + " has been closed.");
        } catch (AccountNotFoundException | CannotCloseLastAccountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleAccountDeposit(Scanner scanner) {
        System.out.println("Enter account ID:");
        long accountId = readLong(scanner);
        System.out.println("Enter amount to deposit:");
        BigDecimal amount = readBigDecimal(scanner);
        try {
            accountService.deposit(accountId, amount);
            System.out.println("Amount " + amount + " deposited to account ID: " + accountId);
        } catch (AccountNotFoundException | InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleAccountTransfer(Scanner scanner) {
        System.out.println("Enter source account ID:");
        long sourceId = readLong(scanner);
        System.out.println("Enter target account ID:");
        long targetId = readLong(scanner);
        System.out.println("Enter amount to transfer:");
        BigDecimal amount = readBigDecimal(scanner);
        try {
            accountService.transfer(sourceId, targetId, amount);
            System.out.println("Amount " + amount + " transferred from account ID " + sourceId + " to account ID " + targetId + ".");
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error executing command ACCOUNT_TRANSFER: error=" + e.getMessage());
        }
    }

    private void handleAccountWithdraw(Scanner scanner) {
        System.out.println("Enter account ID to withdraw from:");
        long accountId = readLong(scanner);
        System.out.println("Enter amount to withdraw:");
        BigDecimal amount = readBigDecimal(scanner);
        try {
            accountService.withdraw(accountId, amount);
            System.out.println("Amount " + amount + " withdrawn from account ID: " + accountId);
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error executing command ACCOUNT_WITHDRAW: error=" + e.getMessage());
        }
    }

    private long readLong(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer number:");
            }
        }
    }

    private BigDecimal readBigDecimal(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return new BigDecimal(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number:");
            }
        }
    }
}

