package io.lightstudio.economy.eco.config;

import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.NumberFormatter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class SettingParams {

    private final LightEco lightEco;
    private final String defaultCurrency = "default-currency.";

    public SettingParams(LightEco lightEco) {
        this.lightEco = lightEco;
    }

    public String getModuleLanguage() {
        return lightEco.getSettings().getConfig().getString("module-language");
    }

    public int getDatabaseSyncInterval() {
        return lightEco.getSettings().getConfig().getInt("database-sync-time") == 0
                ? 120 : lightEco.getSettings().getConfig().getInt("database-sync-time");
    }


    public DefaultCurrency defaultCurrency() {
        return new DefaultCurrency();
    }
    public SettingWrapper mainSettings() {
        return new SettingWrapper();
    }
    public TitleWrapper titleSettings() { return new TitleWrapper(); }


    public class SettingWrapper {
        public SimpleDateFormat getDateFormat() {
            String result = lightEco.getSettings().getConfig().getString("module-language");
            return result != null ? new SimpleDateFormat(result) : new SimpleDateFormat("dd:MM:yyyy");
        }
    }

    public class TitleWrapper {

        public boolean titleEnabled() {
            return lightEco.getSettings().getConfig().getBoolean("title-animation.enable");
        }
        public double titleMin() {
            return lightEco.getSettings().getConfig().getDouble("title-animation.min");
        }
        public String titleStyle() {
            return lightEco.getSettings().getConfig().getString("title-animation.style");
        }
        public int titleDuration() {
            return lightEco.getSettings().getConfig().getInt("title-animation.duration");
        }


        public boolean soundEnabled() {
            return lightEco.getSettings().getConfig().getBoolean("title-animation.sound-settings.enable");
        }
        public String soundType() {
            return lightEco.getSettings().getConfig().getString("title-animation.sound-settings.sound");
        }
        public float soundVolume() {
            return (float) lightEco.getSettings().getConfig().getDouble("title-animation.sound-settings.volume");
        }
        public String soundChannel() {
            return lightEco.getSettings().getConfig().getString("title-animation.sound-settings.channel");
        }
    }


    public class DefaultCurrency {
        public BigDecimal getStartBalance() {
            double startBalance = lightEco.getSettings().getConfig().getDouble(defaultCurrency + "start-balance");
            return NumberFormatter.formatBigDecimal(BigDecimal.valueOf(startBalance));
        }
        public boolean firstJoinMessageEnabled() {
            return lightEco.getSettings().getConfig().getBoolean(defaultCurrency + "enable-first-join-message");
        }
        public String currencyPluralName() {
            return lightEco.getSettings().getConfig().getString(defaultCurrency + "currency-name-plural");
        }
        public String currencySingularName() {
            return lightEco.getSettings().getConfig().getString(defaultCurrency + "currency-name-singular");
        }
        public Integer fractionalDigits() {
            return lightEco.getSettings().getConfig().getInt(defaultCurrency + "fractional-digits");
        }
        public double maxPocketBalance() {
            return lightEco.getSettings().getConfig().getDouble(defaultCurrency + "max-pocket-balance");
        }
    }
}
