package io.lightstudio.economy.eco.implementer;

import io.lightstudio.economy.eco.api.EcoProfile;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PendingTransactions {
    private final UUID playerUUID;
    private final List<Transaction> transactions;

    public PendingTransactions(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public boolean isTransactionProcessed(Transaction transaction, EcoProfile ecoProfile) {
        if (transaction.isProcessed()) {
            return true;
        }
        if (transaction.getType() == Transaction.Type.DEPOSIT) {
            return ecoProfile.getBalance().compareTo(BigDecimal.valueOf(transaction.getAmount())) >= 0;
        } else if (transaction.getType() == Transaction.Type.WITHDRAW) {
            return ecoProfile.getBalance().compareTo(BigDecimal.valueOf(transaction.getAmount())) <= 0;
        }
        return false;
    }

    @Getter
    public static class Transaction {
        private final Type type;
        private final double amount;
        @Setter
        private boolean processed;

        public enum Type {DEPOSIT, WITHDRAW}

        public Transaction(Type type, double amount) {
            this.type = type;
            this.amount = amount;
            this.processed = false;
        }
    }
}