package io.lightstudio.economy.eco.config;

import io.lightstudio.economy.util.manager.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MessageParams {

    private final FileConfiguration config;

    public MessageParams(FileManager selectedLanguage) {
        this.config = selectedLanguage.getConfig();
    }

    public int version() { return config.getInt("version"); }
    // Basic translations
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
    public String depositAllSuccess() { return config.getString("depositAllSuccess"); }
    public String depositFailed() { return config.getString("depositFailed"); }
    public String depositAllFailed() { return config.getString("depositAllFailed"); }
    public String withdrawSuccess() { return config.getString("withdrawSuccess"); }
    public String withdrawFailed() { return config.getString("withdrawFailed"); }
    public String setSuccess() { return config.getString("setSuccess"); }
    public String setFailed() { return config.getString("setFailed"); }
    //  Title Deposit messages
    public String titleDepositCountTitle() { return config.getString("titleDeposit.count.title"); }
    public String titleDepositCountSubtitle() { return config.getString("titleDeposit.count.subtitle"); }
    public String titleDepositFinalTitle() { return config.getString("titleDeposit.final.title"); }
    public String titleDepositFinalSubtitle() { return config.getString("titleDeposit.final.subtitle"); }
    //  Title Withdraw messages
    public String titleWithdrawCountTitle() { return config.getString("titleWithdraw.count.title"); }
    public String titleWithdrawCountSubtitle() { return config.getString("titleWithdraw.count.subtitle"); }
    public String titleWithdrawFinalTitle() { return config.getString("titleWithdraw.final.title"); }
    public String titleWithdrawFinalSubtitle() { return config.getString("titleWithdraw.final.subtitle"); }
    //  Top command messages
    public List<String> topCommandHeader() { return config.getStringList("topCommand.header"); }
    public String topCommandEntry() { return config.getString("topCommand.entry"); }
    public List<String> topCommandFooter() { return config.getStringList("topCommand.footer"); }

}
