package io.lightstudio.economy.eco.tasks;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.manager.QueryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseSynchronisation {

    private final QueryManager queryManager;
    private final ScheduledExecutorService scheduler;

    public DatabaseSynchronisation(QueryManager queryManager) {
        this.queryManager = queryManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startSync(int intervalInMinutes) {
        this.scheduler.scheduleAtFixedRate(this::syncEcoProfilesAsync, intervalInMinutes, intervalInMinutes, TimeUnit.MINUTES);
    }

    public void forceSync() {
        Light.getConsolePrinting().debug("Server shutdown was detected ...");
        Light.getConsolePrinting().debug("Forcing failsafe synchronisation of eco profiles to database");
        syncEcoProfiles();
        Light.getConsolePrinting().debug("All eco profiles have been synchronised to the database");
    }

    private void syncEcoProfilesAsync() {

        long start = System.currentTimeMillis();
        int amount = 0;

        if(LightEco.instance.getEcoProfiles().isEmpty()) {
            Light.getConsolePrinting().debug("No eco profiles to save to database");
            return;
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (EcoProfile ecoProfile : LightEco.instance.getEcoProfiles()) {
            amount++;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                this.queryManager.updateEcoProfileInDatabaseAsync(ecoProfile);
            });
            futures.add(future);
        }

        int finalAmount = amount;
        CompletableFuture.runAsync(() -> {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            long end = System.currentTimeMillis();
            Light.getConsolePrinting().debug("Finished " + finalAmount + " async synchronisation of eco profiles in " + (end - start) + "ms");
        });
    }

    private void syncEcoProfiles() {

        long start = System.currentTimeMillis();

        if(LightEco.instance.getEcoProfiles().isEmpty()) {
            Light.getConsolePrinting().debug("No eco profiles to save to database");
            return;
        }

        for (EcoProfile ecoProfile : LightEco.instance.getEcoProfiles()) {
            this.queryManager.updateEcoProfileInDatabase(ecoProfile);
        }

        long end = System.currentTimeMillis();
        Light.getConsolePrinting().debug("Finished synchronisation of eco profiles in " + (end - start) + "ms");
    }
}
