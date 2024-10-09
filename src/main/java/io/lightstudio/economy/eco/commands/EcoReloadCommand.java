package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class EcoReloadCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("reload");
    }

    @Override
    public String getDescription() {
        return "Reload all the .yml files.";
    }

    @Override
    public String getSyntax() {
        return "/eco reload";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.command.reload";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (commandSender, command, s, args) -> {
            if(!commandSender.hasPermission(getPermission())) {
                return null;
            }
            return getName();
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        LightEco.instance.reload();
        Light.getMessageSender().sendPlayerMessage(LightEco.getMessageParams().moduleReload()
                .replace("#module#", LightEco.instance.getName()), player);

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {

        LightEco.instance.reload();
        Light.getConsolePrinting().print("Reloaded " + LightEco.instance.getName() + " module.");

        return false;
    }
}
