package io.lightplugins.dummy.eco.config;

import io.lightplugins.dummy.util.manager.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageParams {

    private final FileConfiguration config;

    public MessageParams(FileManager selectedLanguage) {
        this.config = selectedLanguage.getConfig();
    }

    public int version() { return config.getInt("version"); }

    public String prefix() { return config.getString("prefix"); }
    public String noPermission() { return config.getString("noPermission"); }
    public String moduleReload() { return config.getString("moduleReload"); }
    public String reloadAll() { return config.getString("reloadAll"); }
    public String wrongSyntax() { return config.getString("wrongSyntax"); }
    public String noNumber() { return config.getString("noNumber"); }
    public String onlyPositive() { return config.getString("onlyPositive"); }
    public String playerNotFound() { return config.getString("playerNotFound"); }
    public String moneyShow() { return config.getString("moneyShow"); }
    public String depositSuccess() { return config.getString("depositSuccess"); }
    public String depositFailed() { return config.getString("depositFailed"); }
    public String withdrawSuccess() { return config.getString("withdrawSuccess"); }
    public String withdrawFailed() { return config.getString("withdrawFailed"); }
    public String setSuccess() { return config.getString("setSuccess"); }
    public String setFailed() { return config.getString("setFailed"); }

}
