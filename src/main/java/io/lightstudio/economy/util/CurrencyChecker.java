package io.lightstudio.economy.util;

import io.lightstudio.economy.eco.LightEco;

import java.math.BigDecimal;

public class CurrencyChecker {
    public static String getCurrency(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ONE) > 0) {
            return LightEco.instance.getVaultImplementer().currencyNamePlural();
        }
        return LightEco.instance.getVaultImplementer().currencyNameSingular();
    }
}
