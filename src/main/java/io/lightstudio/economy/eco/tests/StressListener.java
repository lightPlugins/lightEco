package io.lightstudio.economy.eco.tests;

import io.lightstudio.economy.eco.LightEco;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;

@Deprecated
public class StressListener implements Listener {

    private HashMap<Player, Integer> counter = new HashMap<>();


    // TESTING - only for stress testing (depositing x balance for breaking a "grass" block)
    @EventHandler
    public void onBeak(BlockBreakEvent event) {

        Block block = event.getBlock();

        if(counter.containsKey(event.getPlayer())) {
            counter.put(event.getPlayer(), counter.get(event.getPlayer()) + 1);
        } else {
            counter.put(event.getPlayer(), 1);
        }

        if(block.getType().equals(Material.TALL_GRASS)) {
            event.setCancelled(true);
            EconomyResponse response = LightEco.instance.getVaultImplementer().depositPlayer(event.getPlayer(), 13);

            if(response.transactionSuccess()) {
                if(counter.get(event.getPlayer()) > 10) {
                    event.getPlayer().sendMessage("§aYou have received " + (10*13) + " EUR for breaking " + counter.get(event.getPlayer()) + " a grass block.");
                    counter.remove(event.getPlayer());
                }
            } else {
                event.getPlayer().sendMessage("§cAn error occurred while giving you 13EUR. " + response.errorMessage);
            }
        }
    }



}
