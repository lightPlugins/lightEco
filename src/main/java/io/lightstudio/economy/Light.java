package io.lightstudio.economy;

import com.zaxxer.hikari.HikariDataSource;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.ColorTranslation;
import io.lightstudio.economy.util.ConsolePrinting;
import io.lightstudio.economy.util.MessageSender;
import io.lightstudio.economy.util.SubPlaceholder;
import io.lightstudio.economy.util.database.SQLDatabase;
import io.lightstudio.economy.util.database.impl.MySQLDatabase;
import io.lightstudio.economy.util.database.impl.SQLiteDatabase;
import io.lightstudio.economy.util.database.model.ConnectionProperties;
import io.lightstudio.economy.util.database.model.DatabaseCredentials;
import io.lightstudio.economy.util.hooks.Towny;
import io.lightstudio.economy.util.interfaces.LightModule;
import io.lightstudio.economy.util.manager.FileManager;
import io.lightstudio.economy.util.manager.MultiFileManager;
import io.lightstudio.economy.util.manager.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Light extends JavaPlugin {


    public static Light instance;

    private LightEco lightEco;

    private Map<String, LightModule> modules = new HashMap<>();
    private final ArrayList<SubPlaceholder> subPlaceholders = new ArrayList<>();

    private static MessageSender messageSender;
    public static FileManager database;
    public ColorTranslation colorTranslation;
    private static ConsolePrinting consolePrinting;
    private SQLDatabase pluginDatabase;

    public static boolean isPlaceholderAPI = false;
    public static boolean isTowny = false;

    public HikariDataSource ds;

    public final static String consolePrefix = "§r[light§eEco§r] §r";

    private FileManager settings;


    public void onLoad() {

        instance = this;
        consolePrinting = new ConsolePrinting();
        colorTranslation = new ColorTranslation();
        checkForHooks();
        this.modules = new LinkedHashMap<>();
        database = createNewFile("storage.yml", true);

        if(!this.initDatabase()) {
            getConsolePrinting().print("§4Could not connect to the database. Please check your database.yml");
            getConsolePrinting().print("§4LightEco is shutting down... ");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {

        consolePrinting.print("Loading lightEco modules...");
        messageSender = new MessageSender();
        initModules();
        loadModules();
        registerPlaceHolders();

    }

    public void onDisable() {

        Iterator<LightModule> iterator = this.modules.values().iterator();

        while(iterator.hasNext()) {
            this.unloadModule(iterator.next());
            iterator.remove();
        }

        if(this.pluginDatabase != null) {
            SQLDatabase sqlDatabase = this.pluginDatabase;
            getConsolePrinting().print("Finally, closing database connection...");
            sqlDatabase.close();
            getConsolePrinting().print("§4Database is now closed and connection terminated.");
            getConsolePrinting().print("See you later :)");
        }

        super.onDisable();

    }

    private void loadModules() {
        this.loadModule(lightEco, true);
    }

    private void loadModule(LightModule lightModule, boolean enable) {
        if(lightModule.isEnabled()) {
            consolePrinting.print("Module §e" + lightModule.getName() + "§r already loaded.");
            return;
        }
        consolePrinting.print("Module §e" + lightModule.getName() + "§r is" +
                (enable ? "§a activated" : "§c deactivated"));
        if(enable) { lightModule.enable();  }

    }

    private void unloadModule(LightModule lightModule) {
        if(!lightModule.isEnabled()) {
            return;
        }
        lightModule.disable();
        consolePrinting.print("Successfully unloaded module: §e" + lightModule.getName());
    }

    private void initModules() {
        this.lightEco = new LightEco();
        this.modules.put(this.lightEco.getName(), this.lightEco);
    }

    public FileManager selectLanguage(String languageName, String moduleName) {

        return switch (languageName) {
            case "de" -> {
                consolePrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(Light.instance, moduleName +
                        "/language/de.yml", true);
            }
            case "pl" -> {
                consolePrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(Light.instance, moduleName +
                        "/language/pl.yml", true);
            }
            default -> {
                consolePrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(Light.instance, moduleName +
                        "/language/en.yml", true);
            }
        };
    }

    private void checkForHooks() {
        consolePrinting.print("Checks for third party hooks");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getConsolePrinting().print("PlaceholderAPI is present. Hooking into PlaceholderAPI");
            isPlaceholderAPI = true;
        }

        if (Bukkit.getPluginManager().getPlugin("Towny") != null) {
            getConsolePrinting().print("Towny is present. Hooking into Towny");
            isTowny = true;
        }

    }

    private void registerPlaceHolders() {

        //  this.subPlaceholders.add(new EcoTopPlaceholder());

        if(this.subPlaceholders.isEmpty()) {
            getConsolePrinting().print("No SubPlaceholders found. Skipping Placeholder registration.");
            return;
        }

        PlaceholderManager placeholderManager = new PlaceholderManager(this.subPlaceholders);
        placeholderManager.register();
    }

    private boolean initDatabase() {
        try {
            String databaseType = database.getConfig().getString("storage.type");
            ConnectionProperties connectionProperties = ConnectionProperties.fromConfig(database.getConfig());

            if ("sqlite".equalsIgnoreCase(databaseType)) {
                this.pluginDatabase = new SQLiteDatabase(this, connectionProperties);
                getConsolePrinting().print("Using SQLite (local) database.");
            } else if ("mysql".equalsIgnoreCase(databaseType)) {
                DatabaseCredentials credentials = DatabaseCredentials.fromConfig(database.getConfig());
                this.pluginDatabase = new MySQLDatabase(this, credentials, connectionProperties);
                getConsolePrinting().print("Using MySQL (remote*) database.");
            } else {
                this.getLogger().warning(String.format("Error! Unknown database type: %s. Disabling plugin.", databaseType));
                this.getServer().getPluginManager().disablePlugin(this);
                return false;
            }

            this.pluginDatabase.connect();
        } catch (Exception e) {
            this.getLogger().warning("Could not maintain Database Connection. Disabling plugin.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @NotNull
    public List<FileConfiguration> getFileConfigs(String path) throws IOException {

        final List<FileConfiguration> fileConfigs = new ArrayList<>();

        getMultiFiles(path).forEach(singleFile -> {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(singleFile);
            fileConfigs.add(cfg);
        });

        return fileConfigs;
    }

    @NotNull
    public List<File> getMultiFiles(String path) throws IOException {
        // Add files from the MultiFileManager to the existing files list
        return new ArrayList<>(readMultiFiles(path).getYamlFiles());
    }

    @NotNull
    public MultiFileManager readMultiFiles(String directoryPath) throws IOException {
        return new MultiFileManager(directoryPath);
    }

    @NotNull
    public FileManager createNewFile(String fileName, boolean loadDefaultsOneReload) {
        return new FileManager(this, fileName, loadDefaultsOneReload);
    }

    public static MessageSender getMessageSender() {
        return messageSender;
    }

    public static ConsolePrinting getConsolePrinting() {
        return consolePrinting;
    }

    public SQLDatabase getConnection() {
        return this.pluginDatabase;
    }
}