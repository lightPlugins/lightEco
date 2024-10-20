package io.lightstudio.economy;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.lightstudio.economy.messaging.proxy.receive.ReceiveBackendBalanceUpdate;
import io.lightstudio.economy.messaging.proxy.receive.ReceiveBackendMessage;
import io.lightstudio.economy.messaging.util.ProxyConsolePrinter;
import lombok.Getter;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(id = "lighteconomy", name = "LightEconomy", version = "0.1.0", authors = {"LightStudios"})
public class LightProxy {

    public static final MinecraftChannelIdentifier IDENTIFIER =
            MinecraftChannelIdentifier.from("lightstudio:lighteconomy");

    @Getter
    private final ProxyServer proxy;
    private final Path dataDirectory;
    private final Logger logger;
    @Getter
    private static LightProxy instance;
    @Getter
    private final ProxyConsolePrinter consolePrinter;

    @Inject
    public LightProxy(ProxyServer proxy, @DataDirectory Path dataDirectory, Logger logger) {
        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        instance = this;
        this.consolePrinter = new ProxyConsolePrinter();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        consolePrinter.sendInfo("Registering LightEconomy plugin on Velocity...");
        // Register the plugin channel with the provided identifier
        registerChannelRegistrars();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        consolePrinter.sendInfo("LightEco plugin has been disabled on Velocity!");
        // Cleanup code for Velocity
    }

    public void registerChannelRegistrars() {
        // Register the plugin channel with the provided identifier
        proxy.getChannelRegistrar().register(IDENTIFIER);

        // Register the plugin message listeners
        proxy.getEventManager().register(this, new ReceiveBackendMessage());
        proxy.getEventManager().register(this, new ReceiveBackendBalanceUpdate());

        consolePrinter.sendInfo("Successfully registered custom plugin channels for LightEconomy.");
    }

}
