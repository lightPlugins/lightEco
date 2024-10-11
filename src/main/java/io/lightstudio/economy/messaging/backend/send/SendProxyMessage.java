package io.lightstudio.economy.messaging.backend.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.messaging.util.SubChannelPath;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class SendProxyMessage {

    /**
     * Sends a message through the Bungee network.
     *
     * @param sender the player who is sending the message
     * @param message the message to be sent (from the messages.yml file)
     */
    public static void sendMessageThrowProxy(Player sender, String targetName, String message) {

        // Create a new data output stream
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Write the channel type and message to the data output stream
        out.writeUTF(SubChannelPath.SEND_MESSAGE.getId());
        out.writeUTF(targetName);
        out.writeUTF(MiniMessage.miniMessage().serialize(Light.instance.colorTranslation.universalColor(message)));

        // Send the plugin message through the BungeeCord channel
        Light.getConsolePrinting().debug("Sending message through proxy.");
        sender.sendPluginMessage(Light.instance, Light.instance.getMinecraftChannelIdentifier(), out.toByteArray());


    }
}
