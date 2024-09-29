package io.lightstudio.economy.eco;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.api.LightEcoAPI;
import io.lightstudio.economy.eco.commands.*;
import io.lightstudio.economy.eco.config.MessageParams;
import io.lightstudio.economy.eco.config.SettingParams;
import io.lightstudio.economy.eco.events.OnPlayerJoinServer;
import io.lightstudio.economy.eco.implementer.VaultImplementer;
import io.lightstudio.economy.eco.manager.PrepareProfileLoading;
import io.lightstudio.economy.eco.manager.QueryManager;
import io.lightstudio.economy.eco.tasks.DatabaseSynchronisation;
import io.lightstudio.economy.util.SubCommand;
import io.lightstudio.economy.util.interfaces.LightModule;
import io.lightstudio.economy.util.manager.CommandManager;
import io.lightstudio.economy.util.manager.FileManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.ArrayList;
import java.util.List;

public class LightEco implements LightModule {

    public static LightEco instance;
    private static LightEcoAPI api;
    public boolean isModuleEnabled = false;
    @Getter
    private QueryManager queryManager;
    private DatabaseSynchronisation databaseSynchronisation;

    public final String moduleName = "eco";
    public final String adminPerm = "light." + moduleName + ".admin";
    public final static String tablePrefix = "lighteco_";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    @Getter
    private final List<EcoProfile> ecoProfiles = new ArrayList<>();

    @Getter
    private static SettingParams settingParams;
    @Getter
    private static MessageParams messageParams;

    @Getter
    private VaultImplementer vaultImplementer;

    @Getter
    private FileManager settings;
    @Getter
    private FileManager language;

    @Override
    public void enable() {
        Light.getConsolePrinting().print(
                "Starting the core module " + this.moduleName);
        instance = this;
        Light.getConsolePrinting().print(
                "Creating default files for core module " + this.moduleName);
        initFiles();
        this.settingParams = new SettingParams(this);
        Light.getConsolePrinting().print(
                "Selecting module language for core module " + this.moduleName);
        selectLanguage();
        messageParams = new MessageParams(language);
        Light.getConsolePrinting().print(
                "Registering subcommands for core module " + this.moduleName + "...");
        initSubCommands();
        this.isModuleEnabled = true;
        Light.getConsolePrinting().print(
                "Successfully started core module " + this.moduleName + "!");

        if(!initDatabase()) {
            Light.getConsolePrinting().print("§4Failed to initialize start sequence while enabling module §c" + this.moduleName);
            disable();
        }
        this.databaseSynchronisation = new DatabaseSynchronisation(queryManager);
        this.databaseSynchronisation.startSync(1);

        this.vaultImplementer = new VaultImplementer();
        registerVaultProvider();
        api = new LightEcoAPI();

        new PrepareProfileLoading(getQueryManager());

        registerEvents();

    }

    @Override
    public void disable() {
        this.databaseSynchronisation.forceSync();
        this.isModuleEnabled = false;

        Light.getConsolePrinting().print("Disabled module " + this.moduleName);
    }

    @Override
    public void reload() {
        //initFiles();
        getSettings().reloadConfig(moduleName + "/settings.yml");
        Light.getConsolePrinting().print(moduleName + "/settings.yml");
        selectLanguage();
        Light.getConsolePrinting().print(moduleName + "/language/" + settingParams.getModuleLanguage() + ".yml");
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

    private void selectLanguage() {
        this.language = Light.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initFiles() {
        this.settings = new FileManager(
                Light.instance, moduleName + "/settings.yml", true);
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("bal");
        subCommands.add(new EcoGiveCommand());
        subCommands.add(new EcoSetCommand());
        subCommands.add(new EcoRemoveCommand());
        subCommands.add(new EcoFakeCommand());
        subCommands.add(new EcoDeleteCommand());
        new CommandManager(ecoCommand, subCommands);

    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinServer(), Light.instance);
    }

    public void registerVaultProvider() {
        Economy vaultProvider = vaultImplementer;
        Bukkit.getServicesManager().register(Economy.class, vaultProvider, Light.instance, ServicePriority.Highest);
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp != null) {
            Light.getConsolePrinting().print("Successfully registered Vault provider " + rsp.getProvider().getName());
        }
    }

    private boolean initDatabase() {
        this.queryManager = new QueryManager(Light.instance.getConnection());
        queryManager.createEcoTable();
        return true;
    }

    public static LightEcoAPI getAPI() {
        return api;
    }

}
