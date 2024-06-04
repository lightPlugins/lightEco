package io.lightstudio.economy.util;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;

import java.util.UUID;

public class TestUtil {


    public static void generateFakeData(int amountToFake) {

        for (int i = 0; i < amountToFake; i++) {
            UUID uuid = UUID.randomUUID();

            EcoProfile ecoProfile = new EcoProfile(uuid);
            LightEco.instance.getEcoProfiles().add(ecoProfile);
            LightEco.instance.getQueryManager().prepareNewAccount(uuid, true, 0)
                    .thenAccept(success -> {
                        if (!success) {
                            Light.getConsolePrinting().error("Account preparation failed with account " + uuid + ".");
                        }
                    });

        }
    }
}
