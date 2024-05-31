package io.lightstudio.economy.eco.api.event;

import io.lightstudio.economy.eco.models.TransactionStatus;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VaultDepositEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String target;
    private double amount;
    private boolean isCancelled;
    private TransactionStatus transactionStatus;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public VaultDepositEvent(String target, double amount) {
        this.target = target;
        this.amount = amount;
        this.transactionStatus = TransactionStatus.PENDING;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @FunctionalInterface
    public interface TransactionAction {
        double action(TransactionStatus status, double amount);
    }

    public double getAmount(TransactionAction action) {
        return action.action(this.transactionStatus, this.amount);
    }

    public TransactionStatus getTransactionStatus() {
        return this.transactionStatus;
    }

    public String getTarget() {
        return this.target;
    }

    public double getAmount() {
        return this.amount;
    }

    public double setAmount(double newAmount) {
        return amount = newAmount;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
