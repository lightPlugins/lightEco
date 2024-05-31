package io.lightstudio.economy.eco.manager;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;

import java.util.List;

public class PrepareProfileLoading {

    private final QueryManager queryManager;

    public PrepareProfileLoading(QueryManager queryManager) {
        Light.getConsolePrinting().print("Loading profiles on server start...");
        this.queryManager = queryManager;
        loadProfilesOnServerStart();
    }

    private void loadProfilesOnServerStart () {
        long start = System.currentTimeMillis();
        List<EcoProfile> ecoProfiles = this.queryManager.getAllEcoProfiles();
        for (EcoProfile ecoProfile : ecoProfiles) {
            LightEco.instance.getEcoProfiles().add(ecoProfile);
            Light.getConsolePrinting().debug(
                    "Loaded profile for UUID: " + ecoProfile.getUuid() + " with balance: " + ecoProfile.getBalance());
        }
        Light.getConsolePrinting().debug("Loaded " + ecoProfiles.size() + " profiles in " + (System.currentTimeMillis() - start) + "ms");
    }
}
