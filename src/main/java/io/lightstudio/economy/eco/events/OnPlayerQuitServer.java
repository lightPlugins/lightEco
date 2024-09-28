package io.lightstudio.economy.eco.events;

import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class OnPlayerQuitServer implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {


        // EXPERIMENTAL - Update player profile (balance) from the database on Quit Event.
        //                Not tested and not active !!!

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(uuid);
        if (ecoProfile != null) {
            LightEco.instance.getQueryManager().updateEcoProfileInDatabaseAsync(ecoProfile);
        }
    }


}
