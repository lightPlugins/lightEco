package io.lightstudio.economy.eco.events;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.api.LightEcoAPI;
import io.lightstudio.economy.eco.api.TransactionStatus;
import io.lightstudio.economy.eco.manager.QueryManager;
import io.lightstudio.economy.util.database.SQLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class OnPlayerJoinServer implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(LightEco.getAPI().getEcoProfile(uuid) != null) {

            // EXPERIMENTAL - Update player profile (balance) from the database on Join Event.
            EcoProfile databaseProfile = LightEco.instance.getQueryManager().getEcoProfileFromDatabase(uuid);
            LightEco.getAPI().getEcoProfile(uuid).setBalance(databaseProfile.getBalance());
            return;
        }

        EcoProfile ecoProfile = new EcoProfile(uuid);
        TransactionStatus status = ecoProfile.deposit(LightEco.getSettingParams().defaultCurrency().getStartBalance());
        if(status.equals(TransactionStatus.SUCCESS)) {
            LightEco.instance.getEcoProfiles().add(ecoProfile);
            LightEco.instance.getQueryManager().prepareNewAccount(uuid, true, 0)
                    .thenAccept(success -> {
                        if (success) {
                            Light.getConsolePrinting().debug("Account preparation and generating was successfully.");
                        } else {
                            Light.getConsolePrinting().error("Account preparation failed with account " + uuid + ".");
                            player.kickPlayer("§cAn error occurred while preparing your economy account.\n§cPlease try again later.");
                        }
                    });
        }
    }
}
