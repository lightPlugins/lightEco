package io.lightstudio.economy.eco.implementer;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.eco.api.EcoProfile;
import io.lightstudio.economy.eco.api.event.VaultDepositEvent;
import io.lightstudio.economy.eco.api.event.VaultWithdrawEvent;
import io.lightstudio.economy.eco.api.TransactionStatus;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.hooks.Towny;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VaultImplementer implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "lightEco";
    }

    @Override
    public int fractionalDigits() {
        return LightEco.instance.getSettingParams().defaultCurrency().fractionalDigits();
    }

    @Override
    public String format(double v) {
        BigDecimal bigDecimal = new BigDecimal(v);
        return NumberFormatter.formatForMessages(bigDecimal);
    }

    @Override
    public String currencyNamePlural() {
        return LightEco.instance.getSettingParams().defaultCurrency().currencyPluralName();
    }

    @Override
    public String currencyNameSingular() {
        return LightEco.instance.getSettingParams().defaultCurrency().currencySingularName();
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return LightEco.getAPI().getEcoProfile(offlinePlayer.getUniqueId()) != null;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return NumberFormatter.formatBigDecimal(
                LightEco.getAPI().getEcoProfile(offlinePlayer.getUniqueId()).getBalance()).doubleValue();
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return getBalance(offlinePlayer) >= v;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {

        VaultWithdrawEvent withdrawEvent = new VaultWithdrawEvent(offlinePlayer.getName(), v);

        Bukkit.getScheduler().runTask(Light.instance, ()-> {
            Bukkit.getServer().getPluginManager().callEvent(withdrawEvent);
        });

        v = withdrawEvent.getAmount();

        if(withdrawEvent.isCancelled()) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "Withdraw Event was cancelled by another plugin");
        }

        if(!hasAccount(offlinePlayer)) {
            withdrawEvent.setTransactionStatus(TransactionStatus.ACCOUNT_NOT_FOUND);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player has no account");
        }

        if(!has(offlinePlayer, v)) {
            withdrawEvent.setTransactionStatus(TransactionStatus.INSUFFICIENT_FUNDS);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }

        EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(offlinePlayer.getUniqueId());

        TransactionStatus status = ecoProfile.withdraw(
                NumberFormatter.formatBigDecimal(BigDecimal.valueOf(v)));

        if(status.equals(TransactionStatus.SUCCESS)) {
            withdrawEvent.setTransactionStatus(TransactionStatus.SUCCESS);

            // EXPERIMENTAL: Update the EcoProfile directly in the database after modifying the balance.
            LightEco.instance.getQueryManager().updateEcoProfileInDatabaseAsync(ecoProfile);

            return new EconomyResponse(v, getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, "");
        }

        withdrawEvent.setTransactionStatus(status);
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, status.name());
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {

        VaultDepositEvent depositEvent = new VaultDepositEvent(offlinePlayer.getName(), v);

        Bukkit.getScheduler().runTask(Light.instance, ()-> {
            Bukkit.getServer().getPluginManager().callEvent(depositEvent);
        });

        v = depositEvent.getAmount();

        if(depositEvent.isCancelled()) {
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE,
                    "Deposit Event was cancelled by another plugin");
        }

        if(!hasAccount(offlinePlayer)) {
            depositEvent.setTransactionStatus(TransactionStatus.ACCOUNT_NOT_FOUND);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player has no account");
        }

        EcoProfile ecoProfile = LightEco.getAPI().getEcoProfile(offlinePlayer.getUniqueId());

        TransactionStatus status = ecoProfile.deposit(
                NumberFormatter.formatBigDecimal(BigDecimal.valueOf(v)));

        if (status.equals(TransactionStatus.SUCCESS)) {
            depositEvent.setTransactionStatus(TransactionStatus.SUCCESS);

            // EXPERIMENTAL: Update the EcoProfile directly in the database after modifying the balance.
            LightEco.instance.getQueryManager().updateEcoProfileInDatabaseAsync(ecoProfile);

            return new EconomyResponse(v, getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, "");
        }

        depositEvent.setTransactionStatus(status);
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, status.name());
    }

    /**
     *
     * #########  !!!  Vault´s Bank System  !!!  #########
     * Currently not supported by LightEco
     */

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "Bank system not supported right now");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(s);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    /**
     *  #########  !!!  Duplicated default Vault methods  !!!  #########
     * Double called Methods, that's result in the same outcome.
     *
     */

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    /**
     * #########  !!!  DANGER  !!!  #########
     * These deprecated methods that should never be called by any plugin. !!!!
     * TOWNY stuff ONLY !!!!
     *
     */

    @Override
    public boolean createPlayerAccount(String s) {

        if(Light.isTowny) {
            UUID uuid = Towny.getTownyUUID(s);
            EcoProfile ecoProfile = new EcoProfile(uuid);
            LightEco.instance.getEcoProfiles().add(ecoProfile);
            LightEco.instance.getQueryManager().prepareNewAccount(uuid, true, 1)
                    .thenAccept(success -> {
                        if (success) {
                            Light.getConsolePrinting().debug("Towny account preparation and generating was successful.");
                        } else {
                            Light.getConsolePrinting().error("Account preparation failed with account " + uuid + ".");
                        }
                    });
        }

        Light.getConsolePrinting().error("Method createPlayerAccount(String s) was called with parameter " + s);

        return false;
    }

    @Override
    public boolean hasAccount(String s) {

        if(Light.isTowny) {
            UUID uuid = Towny.getTownyUUID(s);
            return LightEco.getAPI().getEcoProfile(uuid) != null;
        }

        Light.getConsolePrinting().error("Method hasAccount(String s) was called with parameter " + s);

        return false;
    }

    @Override
    public double getBalance(String s) {

        if(Light.isTowny) {

            if(!hasAccount(s)) {
                return 0;
            }

            UUID uuid = Towny.getTownyUUID(s);
            return NumberFormatter.formatBigDecimal(
                    LightEco.getAPI().getEcoProfile(uuid).getBalance()).doubleValue();
        }

        Light.getConsolePrinting().error("Method getBalance(String s) was called with parameter " + s);
        return 0;
    }

    @Override
    public boolean has(String s, double v) {

        if(Light.isTowny) {
            return getBalance(s) >= v;
        }
        Light.getConsolePrinting().error("Method has(String s, double v) was called with parameter " + s + " and " + v);
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {

        if(Light.isTowny) {

            if(!hasAccount(s)) {
                return new EconomyResponse(0, 0,
                        EconomyResponse.ResponseType.FAILURE, "Town has no account");
            }

            if(!has(s, v)) {
                return new EconomyResponse(0, 0,
                        EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            }

            UUID uuid = Towny.getTownyUUID(s);
            TransactionStatus status = LightEco.getAPI().getEcoProfile(uuid).withdraw(
                    NumberFormatter.formatBigDecimal(BigDecimal.valueOf(v)));

            if(status.equals(TransactionStatus.SUCCESS)) {
                return new EconomyResponse(v, getBalance(s),
                        EconomyResponse.ResponseType.SUCCESS, "");
            }

            return new EconomyResponse(0, 0,
                    EconomyResponse.ResponseType.FAILURE, status.name());
        }

        Light.getConsolePrinting().error("Method withdrawPlayer(String s, double v) was called with parameter " + s + " and " + v);

        return new EconomyResponse(0, 0,
                EconomyResponse.ResponseType.FAILURE, "Deprecated method");
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {

        if(Light.isTowny) {

            if(!hasAccount(s)) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Town has no account");
            }

            TransactionStatus status = LightEco.getAPI().getEcoProfile(Towny.getTownyUUID(s)).deposit(
                    NumberFormatter.formatBigDecimal(BigDecimal.valueOf(v)));

            if (status.equals(TransactionStatus.SUCCESS)) {
                return new EconomyResponse(v, getBalance(s),
                        EconomyResponse.ResponseType.SUCCESS, "");
            }

            return new EconomyResponse(0, 0,
                    EconomyResponse.ResponseType.FAILURE, status.name());
        }

        Light.getConsolePrinting().error("Method depositPlayer(String s, double v) was called with parameter " + s + " and " + v);

        return new EconomyResponse(0, 0,
                EconomyResponse.ResponseType.FAILURE, "Deprecated method");
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }
}
