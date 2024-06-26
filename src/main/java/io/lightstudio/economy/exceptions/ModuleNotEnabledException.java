package io.lightstudio.economy.exceptions;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.util.interfaces.LightModule;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException(LightModule module) {
        super(Light.consolePrefix + "The Module §e" + module.getName() + "§r is not enabled");
    }
}
