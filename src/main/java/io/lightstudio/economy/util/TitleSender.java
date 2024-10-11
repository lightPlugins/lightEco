package io.lightstudio.economy.util;

import io.lightstudio.economy.Light;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public class TitleSender {

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

        // Translate MiniMessage color codes
        Component titleComponent = Light.instance.colorTranslation.universalColor(player, title);
        Component subtitleComponent = Light.instance.colorTranslation.universalColor(player, subtitle);

        // Create the title
        Title.Times times = Title.Times.times(java.time.Duration.ofMillis(fadeIn * 50L),
                        java.time.Duration.ofMillis(stay * 50L),
                        java.time.Duration.ofMillis(fadeOut * 50L));

        Title adventureTitle = Title.title(titleComponent, subtitleComponent, times);

        // Send the title using Audience (Adventure API)
        Audience audience = (Audience) player;
        audience.showTitle(adventureTitle);
    }
}