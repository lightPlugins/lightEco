package io.lightplugins.dummy.bank;

import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.bank.config.SettingParams;
import io.lightplugins.dummy.util.SubCommand;
import io.lightplugins.dummy.util.interfaces.LightModule;
import io.lightplugins.dummy.util.manager.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class LightBank implements LightModule {

    public static LightBank instance;
    public boolean isModuleEnabled = false;

    public final String moduleName = "bank";
    public final String adminPerm = "lighteconomy." + moduleName + ".admin";
    public final String tablePrefix = "lightbank_";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public SettingParams settingParams;

    private FileManager settings;
    private FileManager language;


    @Override
    public void enable() {
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Starting " + this.moduleName + " module...");
        instance = this;
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Creating default files for module " + this.moduleName + " module...");
        initFiles();
        this.settingParams = new SettingParams(this);
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Selecting module language for module " + this.moduleName + "...");
        selectLanguage();
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Registering subcommands for module " + this.moduleName + "...");
        initSubCommands();
        this.isModuleEnabled = true;
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Successfully started module " + this.moduleName + "!");

    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() { enable(); }

    @Override
    public boolean isEnabled() {
        return isModuleEnabled;
    }

    @Override
    public String getName() {
        return moduleName;
    }

    public FileConfiguration getSettings() { return this.settings.getConfig(); }

    public FileConfiguration getLanguage() { return this.language.getConfig(); }

    private void initFiles() {
        this.settings = new FileManager(
                Light.instance, moduleName + "/settings.yml", true);
    }

    private void selectLanguage() {
        this.language = Light.instance.selectLanguage(this.settingParams.getModuleLanguage(), moduleName);
    }

    private void initSubCommands() {

    }
}
