package io.lightplugins.dummy.exceptions;

import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.util.interfaces.LightModule;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException(LightModule module) {
        super(Light.consolePrefix + "The Module §e" + module.getName() + "§r is not enabled");
    }
}
