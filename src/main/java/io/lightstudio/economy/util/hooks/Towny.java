package io.lightstudio.economy.util.hooks;

import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import io.lightstudio.economy.Light;

import java.util.UUID;

public class Towny {

    public static UUID getTownyUUID(String account) {
        return TownyEconomyHandler.getTownyObjectUUID(account);
    }

    public static boolean isTownyUUID(UUID uuid) {
        // Towny's closed economy server account.
        if (uuid.equals(UUID.fromString("a73f39b0-1b7c-4930-b4a3-ce101812d926"))) {
            return true;
        }

        if (TownyUniverse.getInstance().hasTown(uuid)) {
            return true;
        }

        return TownyUniverse.getInstance().hasNation(uuid);

        // INFO - not working as expected. I don't know why... :(
        // return TownyUniverse.getInstance().getResident(uuid) != null;

/*      TODO: check if the account is an npc

        Resident resident = TownyUniverse.getInstance().getResident(uuid);
        if(resident != null) {
            return resident.isNPC();
        }
*/
    }

}
