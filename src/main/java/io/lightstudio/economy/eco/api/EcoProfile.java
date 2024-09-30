package io.lightstudio.economy.eco.api;

import io.lightstudio.economy.eco.LightEco;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class EcoProfile {

    private final UUID uuid;
    private BigDecimal balance;
    private final BigDecimal maxBalance;

    public EcoProfile(UUID uuid) {
        this.uuid = uuid;
        this.balance = BigDecimal.ZERO;
        this.maxBalance = BigDecimal.valueOf(LightEco.getSettingParams().defaultCurrency().maxPocketBalance());
    }

    public TransactionStatus deposit(BigDecimal amount) {

        // Check if the amount is zero or negative

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return TransactionStatus.ZERO_AMOUNT;
        }

        // Check if the amount exceeds the maximum balance

        if((balance.doubleValue() + amount.doubleValue()) >= maxBalance.doubleValue()) {
            balance = maxBalance;
            return TransactionStatus.MAX_BALANCE_EXCEEDED;
        }

        // Deposit the amount and return success

        balance = balance.add(amount);
        return TransactionStatus.SUCCESS;
    }

    public TransactionStatus withdraw(BigDecimal amount) {

        // Check if the amount is zero or negative

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return TransactionStatus.ZERO_AMOUNT;
        }

        // Check if the player has enough balance

        if (balance.compareTo(amount) < 0) {
            return TransactionStatus.NOT_ENOUGH_BALANCE;
        }

        // Withdraw the amount and return success

        balance = balance.subtract(amount);
        return TransactionStatus.SUCCESS;
    }

    public TransactionStatus setBalance(BigDecimal amount) {

        if(amount.doubleValue() >= maxBalance.doubleValue()) {
            balance = maxBalance;
            return TransactionStatus.MAX_BALANCE_EXCEEDED;
        }

        balance = amount;
        return TransactionStatus.SUCCESS;
    }

    public String getPlayerName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() == null) {
            return null;
        }
        return offlinePlayer.getName();
    }
}
