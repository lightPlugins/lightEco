package io.lightstudio.economy.messaging.proxy.receive;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.lightstudio.economy.LightProxy;
import io.lightstudio.economy.messaging.util.SubChannelPath;
import net.kyori.adventure.text.minimessage.MiniMessage;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class ReceiveBackendMessage {

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
        String targetName = input.readUTF();
        String message = input.readUTF();

        //  Check if the subchannel is the correct one
        if(!subChannel.equals(SubChannelPath.SEND_MESSAGE.getId())) { return; }

        Player targetPlayer = LightProxy.getInstance().getProxy().getAllPlayers()
                .stream().filter(p -> p.getUsername().equals(targetName)).findFirst().orElse(null);

        // Check if the target player is online one of the backend servers
        if(targetPlayer == null) { return; }

        targetPlayer.sendMessage(MiniMessage.miniMessage().deserialize(message));

    }
}
