package io.lightstudio.economy.util;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageSender {

    public void sendPlayerMessage(String message, Player player) {
        Bukkit.getScheduler().runTask(Light.instance, () -> {
            Audience audience = (Audience) player;
            Component component = Light.instance.colorTranslation.universalColor(player, LightEco.getMessageParams().prefix() + message);
            audience.sendMessage(component);
        });
    }

    public void sendPlayerMessageWithoutPrefix(String message, Player player) {
        Bukkit.getScheduler().runTask(Light.instance, () -> {
            Audience audience = (Audience) player;
            Component component = Light.instance.colorTranslation.universalColor(player, message);
            audience.sendMessage(component);
        });
    }
}
