package io.lightstudio.economy.eco.api;

import io.lightstudio.economy.eco.LightEco;

import java.util.UUID;

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

}
