package io.lightstudio.economy.messaging.backend.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.messaging.util.SubChannelPath;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class SendBalanceUpdate {

    /**
     * Sends a message through the Bungee network.
     *
     * @param sender the player who is sending the message
     * @param targetUUID the UUID of the player who needs update the balance
     * @param balance the new balance of the player
     */
    public static void sendBalanceUpdateThrowProxy(Player sender, String targetUUID, BigDecimal balance) {

        // Create a new data output stream
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Convert BigDecimal to string
        String updateBalance = String.valueOf(balance);

        // Write the channel type and message to the data output stream
        out.writeUTF(SubChannelPath.UPDATE_BALANCE.getId());
        out.writeUTF(targetUUID);
        out.writeUTF(updateBalance);

        // Send the plugin message through the BungeeCord channel
        Light.getConsolePrinting().debug("Sending message through proxy.");
        sender.sendPluginMessage(Light.instance, Light.instance.getMinecraftChannelIdentifier(), out.toByteArray());


    }

}
