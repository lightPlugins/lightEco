package io.lightplugins.dummy;

import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.dummy.eco.LightEco;
import io.lightplugins.dummy.util.ColorTranslation;
import io.lightplugins.dummy.util.DebugPrinting;
import io.lightplugins.dummy.util.MessageSender;
import io.lightplugins.dummy.util.SubPlaceholder;
import io.lightplugins.dummy.util.database.SQLDatabase;
import io.lightplugins.dummy.util.database.impl.MySQLDatabase;
import io.lightplugins.dummy.util.database.impl.SQLiteDatabase;
import io.lightplugins.dummy.util.database.model.ConnectionProperties;
import io.lightplugins.dummy.util.database.model.DatabaseCredentials;
import io.lightplugins.dummy.util.interfaces.LightModule;
import io.lightplugins.dummy.util.manager.FileManager;
import io.lightplugins.dummy.util.manager.MultiFileManager;
import io.lightplugins.dummy.util.manager.PlaceholderManager;
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
    private static DebugPrinting debugPrinting;
    private SQLDatabase pluginDatabase;

    public static boolean isPlaceholderAPI = false;

    public HikariDataSource ds;

    public final static String consolePrefix = "§r[light§eDummy§r] §r";

    private FileManager settings;


    public void onLoad() {

        instance = this;
        debugPrinting = new DebugPrinting();
        colorTranslation = new ColorTranslation();
        checkForHooks();
        this.modules = new LinkedHashMap<>();
        database = createNewFile("settings.yml", true);

        if(!this.initDatabase()) {
            getDebugPrinting().print("§4Could not connect to the database. Please check your database.yml");
            getDebugPrinting().print("§4LightEconomy is shutting down... ");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {

        debugPrinting.print("Loading lightDummy modules...");
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
            sqlDatabase.close();
            getDebugPrinting().print("§4Database is closed");
        }

        super.onDisable();

    }

    private void loadModules() {
        this.loadModule(lightEco, true);
    }

    private void loadModule(LightModule lightModule, boolean enable) {
        if(lightModule.isEnabled()) {
            debugPrinting.print("Module §e" + lightModule.getName() + "§r already loaded.");
            return;
        }
        debugPrinting.print("Module §e" + lightModule.getName() + "§r is" +
                (enable ? "§a activated" : "§c deactivated"));
        if(enable) { lightModule.enable();  }

    }

    private void unloadModule(LightModule lightModule) {
        if(!lightModule.isEnabled()) {
            return;
        }
        lightModule.disable();
        debugPrinting.print("Successfully unloaded module: §e" + lightModule.getName());
    }

    private void initModules() {
        this.lightEco = new LightEco();
        this.modules.put(this.lightEco.getName(), this.lightEco);
    }

    public FileManager selectLanguage(String languageName, String moduleName) {

        return switch (languageName) {
            case "de" -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(Light.instance, moduleName +
                        "/language/de.yml", true);
            }
            case "pl" -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(Light.instance, moduleName +
                        "/language/pl.yml", true);
            }
            default -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(Light.instance, moduleName +
                        "/language/en.yml", true);
            }
        };
    }

    private void checkForHooks() {
        debugPrinting.print("Checks for third party hooks");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getDebugPrinting().print("PlaceholderAPI is present. Hooking into PlaceholderAPI");
            isPlaceholderAPI = true;
        }

    }

    private void registerPlaceHolders() {

        //  this.subPlaceholders.add(new EcoTopPlaceholder());

        if(this.subPlaceholders.isEmpty()) {
            getDebugPrinting().print("No SubPlaceholders found. Skipping Placeholder registration.");
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
                getDebugPrinting().print("Using SQLite (local) database.");
            } else if ("mysql".equalsIgnoreCase(databaseType)) {
                DatabaseCredentials credentials = DatabaseCredentials.fromConfig(database.getConfig());
                this.pluginDatabase = new MySQLDatabase(this, credentials, connectionProperties);
                getDebugPrinting().print("Using MySQL (remote*) database.");
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

    public static DebugPrinting getDebugPrinting() {
        return debugPrinting;
    }

    public SQLDatabase getConnection() {
        return this.pluginDatabase;
    }
}