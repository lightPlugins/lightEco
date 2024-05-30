package io.lightplugins.dummy.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CompositeTabCompleter implements TabCompleter {
    private final Map<String, TabCompleter> subCommandTabCompleters;
    private final List<String> ecoSubCommands; // Liste der Subcommands von /eco

    public CompositeTabCompleter(Map<String, TabCompleter> subCommandTabCompleters, List<String> ecoSubCommands) {
        this.subCommandTabCompleters = subCommandTabCompleters;
        this.ecoSubCommands = ecoSubCommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Wenn keine weiteren Argumente vorhanden sind, geben wir alle Subcommands von /eco zurück
            return ecoSubCommands;
        } else if (args.length > 1) {
            // Wenn weitere Argumente vorhanden sind, delegieren wir die Tab-Completion an den CompositeTabCompleter
            TabCompleter tabCompleter = subCommandTabCompleters.get(args[0]); // Holen Sie den TabCompleter für das angegebene Subcommand
            if (tabCompleter != null) {
                return tabCompleter.onTabComplete(sender, command, alias, args);
            }
        }
        return Collections.emptyList();
    }
}
