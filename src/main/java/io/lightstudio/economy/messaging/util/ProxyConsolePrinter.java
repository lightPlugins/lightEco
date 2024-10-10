package io.lightstudio.economy.messaging.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;

public class ProxyConsolePrinter {

    public static void sendInfo(String message) {
        String prefix = "[light<#ffdc73>Eco<reset>] ";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

    public static void sendWarning(String message) {
        String prefix = "[light<#ffdc73>Eco<reset>] [<yellow>WARNING<reset>] ";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

    public static void sendError(String message) {
        String prefix = "[light<#ffdc73>Eco<reset>] [<red>ERROR<reset>] ";
        Component formattedMessage = MiniMessage.miniMessage().deserialize(prefix + message);
        String ansiMessage = ANSIComponentSerializer.ansi().serialize(formattedMessage);
        System.out.println(ansiMessage);
    }

}
