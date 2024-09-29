package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EcoDeleteCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("delete");
    }

    @Override
    public String getDescription() {
        return "Delete the target player's eco profile from the database.";
    }

    @Override
    public String getSyntax() {
        return "/eco delete <player>";
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.command.delete";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (commandSender, command, s, args) -> {

            if(!commandSender.hasPermission(getPermission())) {
                return null;
            }

            if(args.length == 1) {
                return List.of("delete");
            }

            if (args.length == 2) {
                List<String> offlinePlayerNames = new ArrayList<>();
                for (EcoProfile profile : LightEco.getAPI().getAllEcoProfiles()) {
                    if(profile.getPlayerName() == null) {
                        continue;
                    }
                    offlinePlayerNames.add(profile.getPlayerName());
                }
                return offlinePlayerNames;
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        String targetPlayerName = args[1];

        if(targetPlayerName.equalsIgnoreCase("all")) {
            LightEco.instance.getQueryManager().clearTableAsync()
                    .thenAcceptAsync(cleared -> {
                        if (cleared) {
                            Light.getMessageSender().sendPlayerMessage(
                                    "Successfully cleared the eco profile table.", player);
                            Light.getConsolePrinting().debug("Cleared all eco profiles from Database");
                        } else {
                            Light.getMessageSender().sendPlayerMessage(
                                    "Failed to clear the eco profile table.", player);
                        }
                    });
            LightEco.instance.getEcoProfiles().clear();
            Light.getConsolePrinting().debug("Cleared all eco profiles from RAM");
            return false;
        }

        LightEco.instance.getQueryManager().deletePlayerFromDatabaseAsync(targetPlayerName)
                .thenAcceptAsync(deleted -> {
                    if (deleted) {
                        Light.getMessageSender().sendPlayerMessage(
                                "Successfully deleted " + targetPlayerName + "'s eco profile from the database.", player);
                    } else {
                        Light.getMessageSender().sendPlayerMessage(
                                "Failed to delete " + targetPlayerName + "'s eco profile from the database.", player);
                        Light.getMessageSender().sendPlayerMessage(
                                "Make sure " + targetPlayerName + "'s account is existing before deleting it !", player);
                    }
                });

        LightEco.instance.getEcoProfiles().removeIf(profile -> profile.getPlayerName().equals(targetPlayerName));

        Player targetAccountPlayer = Bukkit.getPlayer(targetPlayerName);

        if(targetAccountPlayer != null) {
            targetAccountPlayer.kickPlayer("§cYour eco profile has been deleted.\n§cPlease rejoin the server!");
        }

        return true;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {

        String targetPlayerName = args[1];

        LightEco.instance.getQueryManager().deletePlayerFromDatabaseAsync(targetPlayerName)
                .thenAcceptAsync(deleted -> {
                    if (deleted) {
                        Light.getConsolePrinting().print(
                                "Successfully deleted " + targetPlayerName + "'s eco profile from the database.");
                    } else {
                        Light.getConsolePrinting().error(
                                "Failed to delete " + targetPlayerName + "'s eco profile from the database.");
                        Light.getConsolePrinting().error(
                                "Make sure " + targetPlayerName + "'s account is existing before deleting it !");
                    }
                });

        return false;
    }
}
