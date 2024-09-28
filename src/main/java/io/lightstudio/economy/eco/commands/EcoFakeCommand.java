package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.util.SubCommand;
import io.lightstudio.economy.util.TestUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class EcoFakeCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("fake");
    }

    @Override
    public String getDescription() {
        return "Register x fake dummys in Database for stress testings. Never use this in productive environments !";
    }

    @Override
    public String getSyntax() {
        return "/eco fake <amountOfFakeAccounts>";
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.command.debug.fake";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return null;
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        int amount = Integer.parseInt(args[1]);
        TestUtil.generateFakeData(amount);
        Light.getMessageSender().sendPlayerMessage(
                "Starting to generate " + amount + " fake eco profiles", player);
        Light.getMessageSender().sendPlayerMessage(
                "Take a look into your console ...", player);

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        return false;
    }
}
