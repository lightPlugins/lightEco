package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.api.TransactionStatus;
import io.lightstudio.economy.util.CurrencyChecker;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EcoSetCommand extends SubCommand {

    @Override
    public List<String> getName() {
        return List.of("set");
    }

    @Override
    public String getDescription() {
        return "Sets the target player an amount of money.";
    }

    @Override
    public String getSyntax() {
        return "/bal set <player> <amount>";
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.command.remove";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (commandSender, command, s, args) -> {

            if(!commandSender.hasPermission(getPermission())) {
                return null;
            }

            if(args.length == 1) {
                return List.of("set");
            }

            if (args.length == 2) {
                List<String> offlinePlayerNames = new ArrayList<>();
                for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                    offlinePlayerNames.add(player.getName());
                }
                return offlinePlayerNames;
            }

            if(args.length == 3) {
                return List.of("<amount>");
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        OfflinePlayer target = Bukkit.getPlayer(args[1]);

        if(target == null) {
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

        EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(target.getUniqueId());
        TransactionStatus status = ecoProfile.setBalance(bg); // Ändern Sie deposit(bg) zu withdraw(bg)
        if(status.equals(TransactionStatus.SUCCESS)) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().setSuccess() // Ändern Sie depositSuccess() zu withdrawSuccess()
                    .replace("#amount#", NumberFormatter.formatForMessages(bg))
                    .replace("#currency#", CurrencyChecker.getCurrency(bg))
                    .replace("#player#", target.getName()), player);
            return false;
        }

        Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().setFailed() // Ändern Sie depositFailed() zu withdrawFailed()
                .replace("#amount#", NumberFormatter.formatForMessages(bg))
                .replace("#currency#", CurrencyChecker.getCurrency(bg))
                .replace("#player#", target.getName())
                .replace("#reason#", status.toString()), player);
        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        OfflinePlayer target = Bukkit.getPlayer(args[1]);

        if(target == null) {
            sender.sendMessage(LightEco.getMessageParams().playerNotFound());
            return false;
        }

        if(!NumberFormatter.isNumber(args[2])) {
            if(!NumberFormatter.isShortNumber(args[2])) {
                sender.sendMessage(LightEco.getMessageParams().noNumber());
                return false;
            }
        }

        BigDecimal bg = NumberFormatter.parseMoney(args[2]);

        if(bg == null) {
            sender.sendMessage(LightEco.getMessageParams().noNumber());
            return false;
        }

        if(!NumberFormatter.isPositiveNumber(bg.doubleValue())) {
            sender.sendMessage(LightEco.getMessageParams().onlyPositive());
            return false;
        }

        EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(target.getUniqueId());
        TransactionStatus status = ecoProfile.setBalance(bg); // Ändern Sie deposit(bg) zu withdraw(bg)
        if(status.equals(TransactionStatus.SUCCESS)) {
            sender.sendMessage(LightEco.getMessageParams().setSuccess() // Ändern Sie depositSuccess() zu withdrawSuccess()
                    .replace("#amount#", NumberFormatter.formatForMessages(bg))
                    .replace("#currency#", CurrencyChecker.getCurrency(bg))
                    .replace("#player#", target.getName()));
            return false;
        }

        sender.sendMessage(LightEco.getMessageParams().setFailed() // Ändern Sie depositFailed() zu withdrawFailed()
                .replace("#amount#", NumberFormatter.formatForMessages(bg))
                .replace("#currency#", CurrencyChecker.getCurrency(bg))
                .replace("#player#", target.getName())
                .replace("#reason#", status.toString()));

        return true;
    }
}
