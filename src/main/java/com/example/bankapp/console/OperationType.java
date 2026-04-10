package com.example.bankapp.console;

public enum OperationType {
    USER_CREATE,
    SHOW_ALL_USERS,
    ACCOUNT_CREATE,
    ACCOUNT_CLOSE,
    ACCOUNT_DEPOSIT,
    ACCOUNT_TRANSFER,
    ACCOUNT_WITHDRAW,
    EXIT;

    public static OperationType fromString(String value) {
        for (OperationType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown operation type: " + value);
    }
}

