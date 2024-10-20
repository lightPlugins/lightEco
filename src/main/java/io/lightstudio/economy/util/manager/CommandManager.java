package io.lightstudio.economy.util.manager;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.util.CompositeTabCompleter;
import io.lightstudio.economy.util.CurrencyChecker;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.SubCommand;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class CommandManager implements CommandExecutor {


    private final ArrayList<SubCommand> subCommands;
    private ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }

    private void registerCommand(PluginCommand command) {
        if (command != null) {
            command.setExecutor(this);
            Map<String, TabCompleter> subCommandTabCompleter = new HashMap<>();
            List<String> ecoSubCommands = new ArrayList<>(); // Liste der Subcommands von /eco

            Light.getConsolePrinting().debug(
                    "Successfully registered command " + command.getName());

            for (SubCommand subCommand : getSubCommands()) {
                TabCompleter tabCompleter = subCommand.registerTabCompleter();
                if (tabCompleter != null) {
                    List<String> subCommandNames = subCommand.getName();
                    for (String subCommandName : subCommandNames) {
                        subCommandTabCompleter.put(subCommandName, tabCompleter);
                        ecoSubCommands.add(subCommandName); // Füge den Subcommand-Namen zur Liste hinzu
                        Light.getConsolePrinting().debug(
                                "Successfully registered tab completer for " + subCommandName);
                    }
                }
            }

            if (!subCommandTabCompleter.isEmpty()) {
                command.setTabCompleter(new CompositeTabCompleter(subCommandTabCompleter, ecoSubCommands));
            }
        }
    }

    public CommandManager(PluginCommand command, ArrayList<SubCommand> subCommands) {
        this.subCommands = subCommands;
        registerCommand(command);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {


            if (args.length > 0) {
                for (SubCommand subCommand : getSubCommands()) {
                    if (subCommand.getName().contains(args[0])) {

                        if (sender instanceof Player player) {
                            if(player.hasPermission(subCommand.getPermission())) {
                                if(args.length != subCommand.maxArgs()) {
                                    Light.getMessageSender().sendPlayerMessage(
                                            LightEco.getMessageParams().wrongSyntax()
                                                    .replace("#syntax#", subCommand.getSyntax()), player);
                                    return false;
                                }
                                try {
                                    subCommand.performAsPlayer(player, args);
                                    return true;
                                } catch (ExecutionException | InterruptedException e) {
                                    throw new RuntimeException("Something went wrong in executing " + Arrays.toString(args), e);
                                }
                            } else {
                                Light.getMessageSender().sendPlayerMessage(
                                        LightEco.getMessageParams().noPermission()
                                                .replace("#permission#", subCommand.getPermission()), player);
                                return false;
                            }
                        }

                        if (sender instanceof ConsoleCommandSender console) {
                            try {
                                subCommand.performAsConsole(console, args);
                                return true;
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException("Something went wrong in executing " + Arrays.toString(args), e);
                            }
                        }
                    }
                }
            }

            List<String> subCommandNames = List.of("bal", "money", "eco", "economy", "balance");

            if(subCommandNames.contains(command.getName()) && sender instanceof Player player) {

                EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(player.getUniqueId());
                BigDecimal balance = ecoProfile.getBalance();

                Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().moneyShow()
                        .replace("#amount#", NumberFormatter.formatForMessages(balance))
                        .replace("#currency#", CurrencyChecker.getCurrency(balance)), player);
                return false;
            }

        return false;
    }
}
