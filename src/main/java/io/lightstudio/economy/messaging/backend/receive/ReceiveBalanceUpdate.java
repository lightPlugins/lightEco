package io.lightstudio.economy.messaging.backend.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.LightEcoAPI;
import io.lightstudio.economy.eco.api.TransactionStatus;
import io.lightstudio.economy.messaging.util.SubChannelPath;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class ReceiveBalanceUpdate implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {

        if(!channel.equals(IDENTIFIER.getName())) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subChannel = input.readUTF();
        String targetUUID = input.readUTF();
        String balance = input.readUTF();

        UUID uuid = UUID.fromString(targetUUID);
        BigDecimal newBalance = new BigDecimal(balance);

        if(!subChannel.equals(SubChannelPath.UPDATE_BALANCE.getId())) {
            return;
        }

        Light.getConsolePrinting().debug("Receiving message from proxy.");

        // Update the balance of the player
        TransactionStatus status = LightEco.getAPI().getEcoProfile(uuid).setBalance(newBalance);

        // Check if the balance was updated successfully
        if(status == TransactionStatus.SUCCESS) {
            Light.getConsolePrinting().debug("Balance updated successfully.");
        } else {
            throw new RuntimeException("Failed to update the balance for " + uuid + " with status " + status + " and balance " + newBalance);
        }
    }
}
