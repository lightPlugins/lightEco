package io.lightplugins.dummy.util;
import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.eco.LightEco;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageSender {

    public void sendPlayerMessage(String message, Player player) {
        Bukkit.getScheduler().runTask(Light.instance, () -> {
            Audience audience = (Audience) player;
            Component component = Light.instance.colorTranslation.universalColor(LightEco.getMessageParams().prefix() + message);
            audience.sendMessage(component);
        });
    }
}
