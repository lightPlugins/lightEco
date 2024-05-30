package io.lightplugins.dummy.bank.config;

import io.lightplugins.dummy.bank.LightBank;

public class SettingParams {

    private final LightBank lightBank;

    public SettingParams(LightBank lightBank) {
        this.lightBank = lightBank;
    }

    public String getModuleLanguage() {
        return lightBank.getSettings().getString("module-language");
    }
}
