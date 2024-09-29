package io.lightstudio.economy.eco.api;

import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.config.SettingParams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class EcoProfile {

    @Getter
    private final UUID uuid;
    private BigDecimal currentBalance;
    @Getter
    private final BigDecimal maxBalance;

    public EcoProfile(UUID uuid) {
        this.uuid = uuid;
        this.currentBalance = BigDecimal.ZERO;
        this.maxBalance = BigDecimal.valueOf(LightEco.getSettingParams().defaultCurrency().maxPocketBalance());
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

    public TransactionStatus setBalance(BigDecimal amount) {

        if(amount.doubleValue() >= maxBalance.doubleValue()) {
            currentBalance = maxBalance;
            return TransactionStatus.MAX_BALANCE_EXCEEDED;
        }

        currentBalance = amount;
        return TransactionStatus.SUCCESS;
    }

    public BigDecimal getBalance() { return currentBalance; }

    public String getPlayerName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() == null) {
            return null;
        }
        return offlinePlayer.getName();
    }
}
