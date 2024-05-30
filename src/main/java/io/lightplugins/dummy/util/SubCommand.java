package io.lightplugins.dummy.util;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class SubCommand {
    public abstract List<String> getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract int maxArgs();
    public abstract String getPermission();
    public abstract TabCompleter registerTabCompleter();
    public abstract boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException;
    public abstract boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException;
}
