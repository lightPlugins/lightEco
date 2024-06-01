package io.lightstudio.economy.util;

import io.lightstudio.economy.Light;
import org.bukkit.Bukkit;

public class ConsolePrinting {

    public void print(String message) {
        Bukkit.getConsoleSender().sendMessage(Light.consolePrefix + message);
    }

    public void debug(String message) {
        Bukkit.getConsoleSender().sendMessage(Light.consolePrefix + "§4[§cDEBUG§4]§r " + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage(Light.consolePrefix + "§4[§cERROR§4]§r §c" + message);
    }

    public void raw(String message) { Bukkit.getConsoleSender().sendMessage(message); }
}
