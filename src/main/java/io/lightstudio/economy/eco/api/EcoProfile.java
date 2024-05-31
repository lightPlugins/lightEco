package io.lightstudio.economy.eco.api;

import io.lightstudio.economy.eco.models.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class EcoProfile {

    private final UUID uuid;
    private BigDecimal currentBalance;
    private final BigDecimal maxBalance;

    public EcoProfile(UUID uuid) {
        this.uuid = uuid;
        this.maxBalance = BigDecimal.valueOf(1000000);
    }

    public TransactionStatus deposit(BigDecimal amount) {

        // Check if the amount is zero or negative

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return TransactionStatus.ZERO_AMOUNT;
        }

        // Check if the amount exceeds the maximum balance

        if((currentBalance.doubleValue() + amount.doubleValue()) >= maxBalance.doubleValue()) {
            currentBalance = maxBalance;
            return TransactionStatus.MAX_BALANCE_EXCEEDED;
        }

        // Deposit the amount and return success

        currentBalance = currentBalance.add(amount);
        return TransactionStatus.SUCCESS;
    }

    public TransactionStatus withdraw(BigDecimal amount) {

        // Check if the amount is zero or negative

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return TransactionStatus.ZERO_AMOUNT;
        }

        // Check if the player has enough balance

        if (currentBalance.compareTo(amount) < 0) {
            return TransactionStatus.NOT_ENOUGH_BALANCE;
        }

        // Withdraw the amount and return success

        currentBalance = currentBalance.subtract(amount);
        return TransactionStatus.SUCCESS;
    }

    public void setBalance(BigDecimal balance) { currentBalance = balance; }

    public BigDecimal getBalance() { return currentBalance; }
    public BigDecimal getMaxBalance() { return maxBalance; }
    public UUID getUuid() { return uuid; }
}
