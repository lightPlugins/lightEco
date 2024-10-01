package io.lightstudio.economy.util;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    public static BigDecimal formatBigDecimal(BigDecimal bd) {
        int fractionalDigits = LightEco.getSettingParams().defaultCurrency().fractionalDigits();
        return bd.setScale(fractionalDigits, RoundingMode.HALF_UP);  // Half-up is the math default round solution
    }

    public static String formatForMessages(BigDecimal number) {
        Locale locale = Locale.getDefault();
        NumberFormat formatter = NumberFormat.getInstance(locale);
        int fractionalDigits = LightEco.getSettingParams().defaultCurrency().fractionalDigits();
        formatter.setMinimumFractionDigits(fractionalDigits); // Ensure at least two decimal places
        formatter.setMaximumFractionDigits(fractionalDigits);
        return formatter.format(formatBigDecimal(number));
    }

    public static boolean isNumber(String s) {
        try {
            if (s.contains(",")) {
                s = s.replace(",", ".");
            }
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShortNumber(String s) {
        return s.endsWith("k") || s.endsWith("m") || s.endsWith("b") || s.endsWith("t");
    }

    public static BigDecimal parseMoney(String amount) {
        try {

            if (amount.contains(",")) {
                amount = amount.replace(",", ".");
            }

            if (amount.matches("^\\d+$")) {
                return formatBigDecimal(new BigDecimal(amount));
            }

            BigDecimal multiplier = BigDecimal.ONE;
            if (amount.endsWith("k")) {
                multiplier = new BigDecimal("1000");
                amount = amount.substring(0, amount.length() - 1);
            } else if (amount.endsWith("m")) {
                multiplier = new BigDecimal("1000000");
                amount = amount.substring(0, amount.length() - 1);
            } else if (amount.endsWith("b")) {
                multiplier = new BigDecimal("1000000000");
                amount = amount.substring(0, amount.length() - 1);
            } else if (amount.endsWith("t")) {
                multiplier = new BigDecimal("1000000000000");
                amount = amount.substring(0, amount.length() - 1);
            }
            return formatBigDecimal(new BigDecimal(amount).multiply(multiplier));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isPositiveNumber(double d) {
        return d > 0;
    }
}