package io.lightstudio.economy.eco.events;

import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.models.TransactionStatus;
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
            return;
        }
        EcoProfile ecoProfile = new EcoProfile(uuid);
        TransactionStatus status = ecoProfile.deposit(LightEco.instance.getSettingParams().defaultCurrency().getStartBalance());
        if(status.equals(TransactionStatus.SUCCESS)) {
            LightEco.instance.getEcoProfiles().add(ecoProfile);
            return;
        }
    }
}
