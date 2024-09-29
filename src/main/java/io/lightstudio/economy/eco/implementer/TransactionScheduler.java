package io.lightstudio.economy.eco.implementer;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionScheduler {
    private final Map<UUID, PendingTransactions> pendingTransactionsMap = new HashMap<>();

    public TransactionScheduler() {
        Bukkit.getScheduler().runTaskTimer(Light.instance, this::processTransactions, 20L, 20L);
    }

    public void addTransaction(UUID playerUUID, PendingTransactions.Transaction transaction) {
        pendingTransactionsMap.computeIfAbsent(playerUUID, PendingTransactions::new).addTransaction(transaction);
    }

    private void processTransactions() {
        for (UUID playerUUID : pendingTransactionsMap.keySet()) {
            PendingTransactions pendingTransactions = pendingTransactionsMap.get(playerUUID);
            EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(playerUUID);

            if (ecoProfile == null) continue;

            Light.getConsolePrinting().debug("PendingTransaction - Processing transactions for " + playerUUID + " with current amount of " + ecoProfile.getBalance() + "EUR.");

/*
            for (PendingTransactions.Transaction transaction : pendingTransactions.getTransactions()) {
                if (!transaction.isProcessed() && !pendingTransactions.isTransactionProcessed(transaction, ecoProfile)) {
                    if (transaction.getType() == PendingTransactions.Transaction.Type.DEPOSIT) {
                        ecoProfile.deposit(BigDecimal.valueOf(transaction.getAmount()));
                        Light.getConsolePrinting().debug("PendingTransaction - Deposited " + transaction.getAmount() + " to " + playerUUID);
                        Light.getConsolePrinting().debug("Current balance D: " + ecoProfile.getBalance());
                    } else if (transaction.getType() == PendingTransactions.Transaction.Type.WITHDRAW) {
                        ecoProfile.withdraw(BigDecimal.valueOf(transaction.getAmount()));
                        Light.getConsolePrinting().debug("PendingTransaction - Withdrawn " + transaction.getAmount() + " from " + playerUUID);
                        Light.getConsolePrinting().debug("Current balance D: " + ecoProfile.getBalance());
                    }
                    transaction.setProcessed(true);
                }
            }
*/
            LightEco.instance.getQueryManager().updateEcoProfileInDatabaseAsync(ecoProfile);
            Light.getConsolePrinting().debug("PendingTransaction - Updated " + playerUUID + " with new balance of " + ecoProfile.getBalance() + "EUR in database successfully.");
            pendingTransactions.clearTransactions();
        }
        pendingTransactionsMap.clear();
    }
}