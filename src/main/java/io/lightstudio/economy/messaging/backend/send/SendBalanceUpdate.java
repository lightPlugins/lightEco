package io.lightstudio.economy.messaging.backend.send;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.messaging.util.SubChannelPath;
import io.lightstudio.economy.util.NumberFormatter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class SendBalanceUpdate {

    /**
     * Sends a message through the Bungee network.
     *
     * @param sender the player who is sending the message
     * @param targetUUID the UUID of the player who needs update the balance
     * @param amount the new balance of the player
     * @param isDeposit if the balance is a deposit or a withdrawal
     */
    public static void sendBalanceUpdateThrowProxy(Player sender, String targetUUID, BigDecimal amount, boolean isDeposit) {

        // Create a new data output stream
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Convert BigDecimal to string
        String updateBalance = String.valueOf(amount);

        // Write the channel type and message to the data output stream
        out.writeUTF(SubChannelPath.UPDATE_BALANCE.getId());
        out.writeUTF(targetUUID);
        out.writeDouble(amount.doubleValue());
        out.writeBoolean(isDeposit);

        // Send the plugin message through the BungeeCord channel
        Light.getConsolePrinting().debug("Adding balance " + NumberFormatter.formatForMessages(amount) + " to " + targetUUID + " through proxy.");
        sender.sendPluginMessage(Light.instance, Light.instance.getMinecraftChannelIdentifier(), out.toByteArray());


    }

}
