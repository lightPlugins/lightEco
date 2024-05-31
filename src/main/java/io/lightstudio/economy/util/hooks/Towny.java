package io.lightstudio.economy.util.hooks;

import com.palmergames.bukkit.towny.TownyEconomyHandler;

import java.util.UUID;

public class Towny {

    public static UUID getTownyUUID(String account) {
        return TownyEconomyHandler.getTownyObjectUUID(account);
    }

}
