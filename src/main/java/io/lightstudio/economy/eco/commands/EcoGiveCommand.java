package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.api.TransactionStatus;
import io.lightstudio.economy.eco.api.animations.EconomyTitle;
import io.lightstudio.economy.util.CurrencyChecker;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.SubCommand;
import io.lightstudio.economy.util.hooks.Towny;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class EcoGiveCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return Arrays.asList("give", "add");
    }

    @Override
    public String getDescription() {
        return "Gives the target player an amount of money.";
    }

    @Override
    public String getSyntax() {
        return "/bal [give,add] <player> <amount>";
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.command.give";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (commandSender, command, s, args) -> {

            if(!commandSender.hasPermission(getPermission())) {
                return null;
            }

            if(args.length == 1) {
                return Arrays.asList("give", "add");
            }

            if (args.length == 2) {
                List<EcoProfile> ecoProfiles = LightEco.getAPI().getAllEcoProfiles();
                // filtering out the profiles that are towny accounts
                return Arrays.asList(ecoProfiles.stream()
                        .map(EcoProfile::getPlayerName)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new));
            }

            if(args.length == 3) {
                return List.of("<amount>");
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        boolean isGlobal = args[1].equalsIgnoreCase("*");
        OfflinePlayer target = Bukkit.getPlayer(args[1]);

        if(target == null && !isGlobal) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().playerNotFound(), player);
            return false;
        }

        if(!NumberFormatter.isNumber(args[2])) {
            if(!NumberFormatter.isShortNumber(args[2])) {
                Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().noNumber(), player);
                return false;
            }
        }

        BigDecimal bg = NumberFormatter.parseMoney(args[2]);

        if(bg == null) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().noNumber(), player);
            return false;
        }

        if(!NumberFormatter.isPositiveNumber(bg.doubleValue())) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().onlyPositive(), player);
            return false;
        }

        List<EcoProfile> ecoProfiles = LightEco.getAPI().getAllEcoProfiles();

        if(isGlobal) {
            // filtering out the profiles that are not towny accounts

            HashMap<EcoProfile, EconomyResponse> failedProfiles = new HashMap<>();

            List<EcoProfile> filteredEcoProfiles = new ArrayList<>();
            for (EcoProfile ecoProfile : ecoProfiles) {

                if (!Towny.isTownyUUID(ecoProfile.getUuid())) {
                    Light.getConsolePrinting().debug("Player Account: " + ecoProfile.getUuid().toString());
                    filteredEcoProfiles.add(ecoProfile);
                    continue;
                }

                Light.getConsolePrinting().debug("Skipping towny, resident or npc: " + ecoProfile.getUuid().toString());
            }

            for (EcoProfile ecoProfile : filteredEcoProfiles) {
                OfflinePlayer targetPlayer = Bukkit.getPlayer(ecoProfile.getUuid());
                if(targetPlayer != null) {
                    EconomyResponse response = LightEco.instance.getVaultImplementer().depositPlayer(targetPlayer, bg.doubleValue());
                    if(!response.transactionSuccess()) {
                        failedProfiles.put(ecoProfile, response);
                    }
                }
            }


            if(!failedProfiles.isEmpty()) {
                Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().depositAllFailed()
                        .replace("#amount#", NumberFormatter.formatForMessages(bg))
                        .replace("#currency#", CurrencyChecker.getCurrency(bg))
                        .replace("#count#",  String.valueOf(failedProfiles.size())), player);

                Light.getConsolePrinting().error(failedProfiles.size() + " of " +
                        ecoProfiles.size() + " accounts failed to deposit. See details below: ");

                for(EcoProfile ecoProfile : failedProfiles.keySet()) {
                    Light.getConsolePrinting().error("Failed to deposit " + bg + " to " +
                            ecoProfile.getPlayerName() + " with reason: " + failedProfiles.get(ecoProfile));
                }

                return false;
            }

            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().depositAllSuccess()
                    .replace("#amount#", NumberFormatter.formatForMessages(bg))
                    .replace("#currency#", CurrencyChecker.getCurrency(bg))
                    .replace("#count#", NumberFormatter.formatForMessages(bg)), player);

            return true;
        }

        EconomyResponse response = LightEco.instance.getVaultImplementer().depositPlayer(target, bg.doubleValue());

        if(response.transactionSuccess()) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().depositSuccess()
                    .replace("#amount#", NumberFormatter.formatForMessages(bg))
                    .replace("#currency#", CurrencyChecker.getCurrency(bg))
                    .replace("#player#", target.getName()), player);

            EconomyTitle economyTitle = new EconomyTitle.Builder(target.getPlayer())
                    .setAmountToCount(bg).setIsDeposit(true).build();
            economyTitle.startCount();

            return false;
        }

        Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().depositFailed()
                .replace("#amount#", NumberFormatter.formatForMessages(bg))
                .replace("#currency#", CurrencyChecker.getCurrency(bg))
                .replace("#player#", target.getName())
                .replace("#reason#", response.errorMessage), player);

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {

        boolean isGlobal = args[1].equalsIgnoreCase("*");
        OfflinePlayer target = Bukkit.getPlayer(args[1]);

        if(target == null && !isGlobal) {
            // sender.sendMessage(LightEco.getMessageParams().playerNotFound());
            Light.getConsolePrinting().error("Player not found: " + args[1]);
            return false;
        }

        if(!NumberFormatter.isNumber(args[2])) {
            if(!NumberFormatter.isShortNumber(args[2])) {
                // sender.sendMessage(LightEco.getMessageParams().noNumber());
                Light.getConsolePrinting().error("Not a number: " + args[2]);
                return false;
            }
        }

        BigDecimal bg = NumberFormatter.parseMoney(args[2]);

        if(bg == null) {
            // sender.sendMessage(LightEco.getMessageParams().noNumber());
            Light.getConsolePrinting().error("Not a number: " + args[2]);
            return false;
        }

        if(!NumberFormatter.isPositiveNumber(bg.doubleValue())) {
            // sender.sendMessage(LightEco.getMessageParams().onlyPositive());
            Light.getConsolePrinting().error("Only positive numbers allowed: " + bg);
            return false;
        }

        List<EcoProfile> ecoProfiles = LightEco.getAPI().getAllEcoProfiles();

        if(isGlobal) {
            // TODO: filtering out the profiles that are not towny accounts
            // currently only online players on the target Server can be paid globally (Vaults no uuid system)
            // TODO: Cross server global payment support -> Velocity Proxy Messaging

            HashMap<EcoProfile, EconomyResponse> failedProfiles = new HashMap<>();

            List<EcoProfile> filteredEcoProfiles = new ArrayList<>();
            for (EcoProfile ecoProfile : ecoProfiles) {

                if (!Towny.isTownyUUID(ecoProfile.getUuid())) {
                    Light.getConsolePrinting().debug("Player Account: " + ecoProfile.getUuid().toString());
                    filteredEcoProfiles.add(ecoProfile);
                    continue;
                }

                Light.getConsolePrinting().debug("Skipping towny, resident or npc: " + ecoProfile.getUuid().toString());
            }

            for (EcoProfile ecoProfile : filteredEcoProfiles) {
                OfflinePlayer targetPlayer = Bukkit.getPlayer(ecoProfile.getUuid());
                if(targetPlayer != null) {
                    EconomyResponse response = LightEco.instance.getVaultImplementer().depositPlayer(targetPlayer, bg.doubleValue());
                    if(!response.transactionSuccess()) {
                        failedProfiles.put(ecoProfile, response);
                    }
                }
            }

            if(!failedProfiles.isEmpty()) {
                Light.getConsolePrinting().error(failedProfiles.size() + " of " +
                        filteredEcoProfiles.size() + " accounts failed to deposit. See details below: ");

                for(EcoProfile ecoProfile : failedProfiles.keySet()) {
                    Light.getConsolePrinting().error("Failed to deposit " + NumberFormatter.formatForMessages(bg)
                            + " to " + ecoProfile.getPlayerName() + " with reason: " + failedProfiles.get(ecoProfile));
                }

                return false;
            }

            Light.getConsolePrinting().print("Successfully deposited "
                    + NumberFormatter.formatForMessages(bg)
                    + " to "
                    + filteredEcoProfiles.size()
                    + " registered account(s).");

            return true;
        }

        EconomyResponse response = LightEco.instance.getVaultImplementer().depositPlayer(target, bg.doubleValue());

        if(response.transactionSuccess()) {
            Light.getConsolePrinting().print("Successfully deposited "
                    + NumberFormatter.formatForMessages(bg)
                    + " "
                    + CurrencyChecker.getCurrency(bg)
                    + " to "
                    + target.getName());
            return false;
        }

        Light.getConsolePrinting().print("Failed deposited "
                + NumberFormatter.formatForMessages(bg)
                + " "
                + CurrencyChecker.getCurrency(bg)
                + " to "
                + target.getName()
                + " with reason: " + response.errorMessage);
        return true;
    }
}
