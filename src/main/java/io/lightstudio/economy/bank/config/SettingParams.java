package io.lightstudio.economy.bank.config;

import io.lightstudio.economy.bank.LightBank;

public class SettingParams {

    private final LightBank lightBank;

    public SettingParams(LightBank lightBank) {
        this.lightBank = lightBank;
    }

    public String getModuleLanguage() {
        return lightBank.getSettings().getString("module-language");
    }
}
