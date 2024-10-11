package io.lightstudio.economy.messaging.backend.receive;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.lightstudio.economy.Light;
import io.lightstudio.economy.messaging.util.SubChannelPath;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class ReceiveProxyMessage implements PluginMessageListener {

    // This is just a template for the plugin message listener.
    // In this package (receive) can receive the backend servers something
    // from the proxy via PluginMessageListener event.

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {


        if(!channel.equals(IDENTIFIER.getName())) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subChannel = input.readUTF();
        String message = input.readUTF();

        if(!subChannel.equals(SubChannelPath.SEND_MESSAGE.getId())) {
            return;
        }

        Light.getConsolePrinting().debug("Receiving message from proxy.");
        Light.getMessageSender().sendPlayerMessage(message, player);


    }
}
