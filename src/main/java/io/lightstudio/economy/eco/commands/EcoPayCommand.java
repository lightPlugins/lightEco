package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.hooks.Towny;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EcoPayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player) {

            // /pay player amount
            if(args.length == 2) {

                String playerName = args[0];

                if(!NumberFormatter.isNumber(args[1])) {
                    if(!NumberFormatter.isShortNumber(args[1])) {
                        Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().noNumber(), player);
                        return false;
                    }
                }

                BigDecimal bg = NumberFormatter.parseMoney(args[1]);

                if(bg == null) {
                    Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().noNumber(), player);
                    return false;
                }

                if(!NumberFormatter.isPositiveNumber(bg.doubleValue())) {
                    Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().onlyPositive(), player);
                    return false;
                }

                List<EcoProfile> ecoProfiles = LightEco.getAPI().getAllEcoProfiles();
                List<EcoProfile> filteredEcoProfiles = new ArrayList<>();

                for (EcoProfile ecoProfile : ecoProfiles) {

                    if (!Towny.isTownyUUID(ecoProfile.getUuid())) {
                        Light.getConsolePrinting().debug("Player Account: " + ecoProfile.getUuid().toString());
                        filteredEcoProfiles.add(ecoProfile);
                        continue;
                    }

                    Light.getConsolePrinting().debug("Skipping towny, resident or npc: " + ecoProfile.getUuid().toString());
                }

                List<String> allNames = Arrays.asList(filteredEcoProfiles.stream()
                        .map(EcoProfile::getPlayerName)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new));


                if(!allNames.contains(playerName)) {
                    Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().playerNotFound()
                            .replace("#player#", playerName), player);
                    return false;
                }



                return true;


            } else {
                Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().wrongSyntax()
                        .replace("#syntax#", "/pay <playername> <amount>"), player);
                return false;
            }
        }



        return false;
    }
}
