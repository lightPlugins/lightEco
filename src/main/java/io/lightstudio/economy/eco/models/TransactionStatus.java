package io.lightstudio.economy.eco.models;

public enum TransactionStatus {
    SUCCESS,
    NOT_ENOUGH_BALANCE,
    ACCOUNT_NOT_FOUND,
    INVALID_AMOUNT,
    INSUFFICIENT_FUNDS,
    ZERO_AMOUNT,
    MAX_BALANCE_EXCEEDED,
    ERROR,
    PENDING
}
