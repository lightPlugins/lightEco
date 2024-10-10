package io.lightstudio.economy.messaging.proxy.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.messaging.util.PluginMessagePath;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class SendProxyMessage {

    /**
     * Sends a message through the Bungee network.
     *
     * @param sender the player who is sending the message
     * @param message the message to be sent
     */
    public void sendMessageThrowProxy(Player sender, String targetName, String message) {

        // Create a new data output stream
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Write the channel type, player name, and message to the data output stream
        out.writeUTF(PluginMessagePath.PAY.getType());
        out.writeUTF(targetName);
        out.writeUTF(MiniMessage.miniMessage().serialize(Light.instance.colorTranslation.universalColor(message)));

        // Send the plugin message through the BungeeCord channel
        sender.sendPluginMessage(Light.instance, IDENTIFIER.getId(), out.toByteArray());

    }

}
