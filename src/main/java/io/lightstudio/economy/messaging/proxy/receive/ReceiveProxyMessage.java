package io.lightstudio.economy.messaging.proxy.receive;


import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;

import static io.lightstudio.economy.LightProxy.IDENTIFIER;

public class ReceiveProxyMessage{

    @Subscribe
    public void onProxyMessage(PluginMessageEvent event) {

        // Check if the identifier matches first, no matter the source.
        // this allows setting all messages to IDENTIFIER as handled,
        // preventing any client-originating messages from being forwarded.
        if (!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }


    }
}
