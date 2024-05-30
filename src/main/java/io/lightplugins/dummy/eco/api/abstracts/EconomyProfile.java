package io.lightplugins.dummy.eco.api.abstracts;

import java.util.UUID;

public abstract class EconomyProfile {

    public UUID uuid;
    public double balance;
    public int isPlayer;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getIsPlayer() {
        return isPlayer;
    }

    public void setIsPlayer(int isPlayer) {
        this.isPlayer = isPlayer;
    }

}
