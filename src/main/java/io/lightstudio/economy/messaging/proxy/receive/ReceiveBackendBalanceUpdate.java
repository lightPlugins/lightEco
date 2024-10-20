package io.lightstudio.economy.messaging.proxy.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.lightstudio.economy.LightProxy;
import io.lightstudio.economy.messaging.util.SubChannelPath;

import java.util.UUID;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class ReceiveBackendBalanceUpdate {

    @Subscribe
    public void onPluginMessageFromPlugin(PluginMessageEvent event) {

        // Check if the identifier matches first, no matter the source.
        // this allows setting all messages to IDENTIFIER as handled,
        // preventing any client-originating messages from being forwarded.
        if (!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        // Client side plugin message handling
        if(!(event.getSource() instanceof ServerConnection)) { return; }

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String subChannel = input.readUTF();
        String targetUUID = input.readUTF();

        //  Check if the subchannel is the correct one
        if(!subChannel.equals(SubChannelPath.UPDATE_BALANCE.getId())) { return; }

        Player targetPlayer = LightProxy.getInstance().getProxy().getAllPlayers()
                .stream().filter(p -> p.getUniqueId().equals(UUID.fromString(targetUUID))).findFirst().orElse(null);


        if(targetPlayer == null) {
            return;
        }

        // Forward the balance update to the target server + player
        ServerConnection serverConnection = targetPlayer.getCurrentServer().orElse(null);
        if (serverConnection != null) {
            serverConnection.sendPluginMessage(IDENTIFIER, event.getData());
        } else {
            LightProxy.getInstance().getConsolePrinter().sendError("Player " + targetPlayer.getUsername() + " is not connected to any server.");
            LightProxy.getInstance().getConsolePrinter().sendError("Could not update balance for player " + targetPlayer.getUsername());
            throw new RuntimeException("Player " + targetPlayer.getUsername() + " is not connected to any server.");
        }
    }

}
