package io.lightplugins.dummy.util.manager;

import io.lightplugins.dummy.Light;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class FileManager {

    /*
     *
     * Configuration-Manager by lightPlugins © 2023
     *
     */

    private final JavaPlugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String configName;
    private final boolean loadDefaultsOneReload;

    public FileManager(JavaPlugin plugin, String configName, boolean loadDefaultsOnReload) {
        this.plugin = plugin;
        this.loadDefaultsOneReload = loadDefaultsOnReload;
        this.configName = configName;
        saveDefaultConfig(configName);

    }

    public void reloadConfig(String configName) {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), configName);

        this.plugin.reloadConfig();

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(configName);
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if(this.dataConfig == null)
            reloadConfig(configName);

        return this.dataConfig;

    }

    public void saveConfig() {
        if(this.dataConfig == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            throw new RuntimeException("[FileManager] §4Could not save config to §c", e);
        }
    }

    private void saveDefaultConfig(String configName) {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), this.configName);

        if (!this.configFile.exists()) {
            this.plugin.saveResource(configName, false);
        } else {
            // Merge the default config into the existing config

            if(loadDefaultsOneReload) {
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(Objects.requireNonNull(this.plugin.getResource(configName))));
                FileConfiguration existingConfig = getConfig();
                for (String key : defaultConfig.getKeys(true)) {
                    if (!existingConfig.getKeys(true).contains(key)) {
                        Bukkit.getConsoleSender().sendMessage(Light.consolePrefix +
                                "[FileManager] Found §enon existing config key§r. Adding §e" + key + " §rinto §e" + configName);
                        existingConfig.set(key, defaultConfig.get(key));

                    }
                }

                try {

                    existingConfig.save(configFile);
                    Bukkit.getConsoleSender().sendMessage(Light.consolePrefix +
                            "[FileManager] Your config §e" + configName + " §ris up to date.");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                saveConfig();
            }
        }
    }
}
