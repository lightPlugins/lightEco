package io.lightstudio.economy.eco.api.event;

import io.lightstudio.economy.eco.api.TransactionStatus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VaultSetEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final String target;
    @Getter
    private double amount;
    private boolean isCancelled;
    @Getter
    @Setter
    private TransactionStatus transactionStatus;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public VaultSetEvent(String target, double amount) {
        this.target = target;
        this.amount = amount;
        this.transactionStatus = TransactionStatus.PENDING;
    }

    @FunctionalInterface
    public interface TransactionAction {
        double action(TransactionStatus status, double amount);
    }

    public double getAmount(TransactionAction action) {
        return action.action(this.transactionStatus, this.amount);
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
