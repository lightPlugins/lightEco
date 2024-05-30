package io.lightplugins.dummy.eco;

import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.eco.commands.DummyCommand;
import io.lightplugins.dummy.eco.config.MessageParams;
import io.lightplugins.dummy.eco.config.SettingParams;
import io.lightplugins.dummy.eco.manager.QueryManager;
import io.lightplugins.dummy.util.SubCommand;
import io.lightplugins.dummy.util.interfaces.LightModule;
import io.lightplugins.dummy.util.manager.CommandManager;
import io.lightplugins.dummy.util.manager.FileManager;
import io.lightplugins.vaulty.api.economy.VaultyEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;

public class LightEco implements LightModule {

    public static LightEco instance;
    public boolean isModuleEnabled = false;
    public Economy economy = null;
    private QueryManager queryManager;
    public static VaultyEconomy economyVaultyService;
    public static Economy economyVaultService;

    public final String moduleName = "eco";
    public final String adminPerm = "light." + moduleName + ".admin";
    public final static String tablePrefix = "lighteco_";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    private SettingParams settingParams;
    private static MessageParams messageParams;

    public VaultyEconomy vaultyProvider;

    private net.milkbowl.vault.economy.Economy vaultProvider;

    private FileManager settings;
    private FileManager language;

    @Override
    public void enable() {
        Light.getDebugPrinting().print(
                "Starting the core module " + this.moduleName);
        instance = this;
        Light.getDebugPrinting().print(
                "Creating default files for core module " + this.moduleName);
        initFiles();
        this.settingParams = new SettingParams(this);
        Light.getDebugPrinting().print(
                "Selecting module language for core module " + this.moduleName);
        selectLanguage();
        messageParams = new MessageParams(language);
        Light.getDebugPrinting().print(
                "Registering subcommands for core module " + this.moduleName + "...");
        initSubCommands();
        this.isModuleEnabled = true;
        Light.getDebugPrinting().print(
                "Successfully started core module " + this.moduleName + "!");

        if(!initDatabase()) {
            Light.getDebugPrinting().print("§4Failed to initialize start sequence while enabling module §c" + this.moduleName);
            disable();
        }


        registerEvents();
        RegisteredServiceProvider<VaultyEconomy> vaultyRSP =
                Bukkit.getServer().getServicesManager().getRegistration(VaultyEconomy.class);
        RegisteredServiceProvider<Economy> vaultRSP =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        economyVaultyService = vaultyRSP.getProvider();
        economyVaultService = vaultRSP.getProvider();

        //getQueryManager().createEcoTable();

    }

    @Override
    public void disable() {
        this.isModuleEnabled = false;
        Light.getDebugPrinting().print("Disabled module " + this.moduleName);
    }

    @Override
    public void reload() {
        //initFiles();
        getSettings().reloadConfig(moduleName + "/settings.yml");
        Light.getDebugPrinting().print(moduleName + "/settings.yml");
        selectLanguage();
        Light.getDebugPrinting().print(moduleName + "/language/" + settingParams.getModuleLanguage() + ".yml");
        getLanguage().reloadConfig(moduleName + "/language/" + settingParams.getModuleLanguage() + ".yml");
    }

    @Override
    public boolean isEnabled() {
        return this.isModuleEnabled;
    }

    @Override
    public String getName() {
        return moduleName;
    }

    public FileManager getSettings() { return settings; }

    public FileManager getLanguage() { return language; }

    private void selectLanguage() {
        this.language = Light.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initFiles() {
        this.settings = new FileManager(
                Light.instance, moduleName + "/settings.yml", true);
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("eco");
        subCommands.add(new DummyCommand());
        new CommandManager(ecoCommand, subCommands);

    }

    public QueryManager getQueryManager() { return this.queryManager; }

    public void registerEvents() {
        //Bukkit.getPluginManager().registerEvents(new CreatePlayerOnJoin(), Light.instance);
    }

    public SettingParams getSettingParams() {
        return settingParams;
    }

    public static MessageParams getMessageParams() {
        return messageParams;
    }

    private boolean initDatabase() {
        this.queryManager = new QueryManager(Light.instance.getConnection());
        queryManager.createEcoTable();
        return true;
    }
}
