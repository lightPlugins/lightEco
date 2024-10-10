package io.lightstudio.economy.eco.commands;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.CurrencyChecker;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class EcoTopCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("top");
    }

    @Override
    public String getDescription() {
        return "Show top x richest players";
    }

    @Override
    public String getSyntax() {
        return "/eco top";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "lighteconomy.eco.command.top";
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

        int balTopAmount = LightEco.getSettingParams().mainSettings().getBalTopAmount();
        // make sure the amount is not 0
        balTopAmount = balTopAmount == 0 ? 3 : balTopAmount;

        // Header print
        LightEco.getMessageParams().topCommandHeader().forEach(header -> {
            Light.getMessageSender().sendPlayerMessageWithoutPrefix(header, player);
        });

        // position atomic integer
        AtomicInteger position = new AtomicInteger(1);
        // Top x the richest players print
        LightEco.getAPI().getTopEcoProfiles(balTopAmount).forEach(profile -> {
            Light.getMessageSender().sendPlayerMessageWithoutPrefix(LightEco.getMessageParams().topCommandEntry()
                    .replace("#position#", String.valueOf(position))
                    .replace("#name#", profile.getPlayerName())
                    .replace("#amount#", NumberFormatter.formatForMessages(profile.getBalance()))
                    .replace("#currency#", CurrencyChecker.getCurrency(profile.getBalance())), player);
            position.getAndIncrement();
        });

        // Footer print
        LightEco.getMessageParams().topCommandFooter().forEach(footer -> {
            Light.getMessageSender().sendPlayerMessageWithoutPrefix(footer, player);
        });

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {


        int balTopAmount = LightEco.getSettingParams().mainSettings().getBalTopAmount();
        // make sure the amount is not 0
        balTopAmount = balTopAmount == 0 ? 3 : balTopAmount;


        // TODO: Add console messages for the top command
        // TODO: Add translations for the top command for console


        return false;
    }
}
