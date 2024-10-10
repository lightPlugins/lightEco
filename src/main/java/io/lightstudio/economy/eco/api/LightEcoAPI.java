package io.lightstudio.economy.eco.api;

import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.hooks.Towny;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class LightEcoAPI {

    public EcoProfile getEcoProfile(UUID uuid) {
        EcoProfile result = null;
        for (EcoProfile profile : LightEco.instance.getEcoProfiles()) {
            if (profile.getUuid().equals(uuid)) {
                result = profile;
                break;
            }
        }
        return result;
    }

    public List<EcoProfile> getAllEcoProfiles() {
        return LightEco.instance.getEcoProfiles();
    }

    public Stream<EcoProfile> getTopEcoProfiles(int amount) {
        Stream<EcoProfile> profiles = getAllEcoProfiles().stream();

        // remove towny profiles
        profiles = profiles.filter(p -> !Towny.isTownyUUID(p.getUuid()));
        // avoid null names (eco fake profiles)
        profiles = profiles.filter(p -> p.getPlayerName() != null);
        // Big Decimals comparing
        return profiles.sorted((p1, p2) -> p2.getBalance().compareTo(p1.getBalance()));

    }

}
