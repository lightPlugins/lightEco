package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.api.TransactionStatus;
import io.lightstudio.economy.eco.implementer.PendingTransactions;
import io.lightstudio.economy.eco.implementer.TransactionScheduler;
import io.lightstudio.economy.util.CurrencyChecker;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.SubCommand;
import net.milkbowl.vault.economy.EconomyResponse;
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

        if (target == null) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().playerNotFound(), player);
            return false;
        }

        if (!NumberFormatter.isNumber(args[2])) {
            if (!NumberFormatter.isShortNumber(args[2])) {
                Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().noNumber(), player);
                return false;
            }
        }

        BigDecimal newBalance = NumberFormatter.parseMoney(args[2]);

        if (newBalance == null) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().noNumber(), player);
            return false;
        }

        if (!NumberFormatter.isPositiveNumber(newBalance.doubleValue())) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().onlyPositive(), player);
            return false;
        }

        EconomyResponse economyResponse = LightEco.instance.getVaultImplementer().setPlayer(target, newBalance.doubleValue());

        if(economyResponse.transactionSuccess()) {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().setSuccess()
                    .replace("#amount#", NumberFormatter.formatForMessages(newBalance))
                    .replace("#currency#", CurrencyChecker.getCurrency(newBalance))
                    .replace("#player#", target.getName()), player);
            return true;
        } else {
            Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().setFailed()
                    .replace("#amount#", NumberFormatter.formatForMessages(newBalance))
                    .replace("#currency#", CurrencyChecker.getCurrency(newBalance))
                    .replace("#player#", target.getName())
                    .replace("#reason#", economyResponse.errorMessage), player);
        }

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        OfflinePlayer target = Bukkit.getPlayer(args[1]);

        if(target == null) {
            // sender.sendMessage(LightEco.getMessageParams().playerNotFound());
            Light.getConsolePrinting().print("The player " + args[1] + " hasn't been found.");
            return false;
        }

        if(!NumberFormatter.isNumber(args[2])) {
            if(!NumberFormatter.isShortNumber(args[2])) {
                // sender.sendMessage(LightEco.getMessageParams().noNumber());
                Light.getConsolePrinting().print("The number " + args[2] + " is not a number.");
                return false;
            }
        }

        BigDecimal bg = NumberFormatter.parseMoney(args[2]);

        if(bg == null) {
            // sender.sendMessage(LightEco.getMessageParams().noNumber());
            Light.getConsolePrinting().print("The number " + args[2] + " is not a number.");
            return false;
        }

        if(!NumberFormatter.isPositiveNumber(bg.doubleValue())) {
            // sender.sendMessage(LightEco.getMessageParams().onlyPositive());
            Light.getConsolePrinting().print("The number " + args[2] + " is not a positive number.");
            return false;
        }

        BigDecimal currentBalance = LightEco.getAPI().getEcoProfile(target.getUniqueId()).getBalance();

        if (currentBalance.compareTo(bg) < 0) {

            //                      50          10  = 40
            BigDecimal difference = bg.subtract(currentBalance);
            EconomyResponse response = LightEco.instance.getVaultImplementer().depositPlayer(target, difference.doubleValue());
            if (response.transactionSuccess()) {
                Light.getConsolePrinting().print("The player "
                        + target.getName()
                        + " has been added "
                        + difference + " "
                        + CurrencyChecker.getCurrency(difference)
                        + " to his account.");
            } else {
                Light.getConsolePrinting().print("The player "
                        + target.getName()
                        + " hasn't been added "
                        + difference + " "
                        + CurrencyChecker.getCurrency(difference)
                        + " to his account. "
                        + "Reason: " + response.errorMessage);
            }
        } else if (currentBalance.compareTo(bg) > 0) {
            BigDecimal difference = currentBalance.subtract(bg);
            EconomyResponse response = LightEco.instance.getVaultImplementer().withdrawPlayer(target, difference.doubleValue());
            if (response.transactionSuccess()) {
                Light.getConsolePrinting().print("The player "
                        + target.getName()
                        + " has been removed "
                        + difference + " "
                        + CurrencyChecker.getCurrency(difference)
                        + " from his account.");
            } else {
                Light.getConsolePrinting().print("The player "
                        + target.getName()
                        + " hasn't been removed "
                        + difference + " "
                        + CurrencyChecker.getCurrency(difference)
                        + " from his account. "
                        + "Reason: " + response.errorMessage);
            }
        } else {
            // Maybe extra message for same balance and set amount.
            // Just a test message here.
            Light.getConsolePrinting().print("Nothing has changed. Same current balance as the set amount");
        }

        return true;
    }
}
