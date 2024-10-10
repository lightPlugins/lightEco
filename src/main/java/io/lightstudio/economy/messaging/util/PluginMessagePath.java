package io.lightstudio.economy.messaging.util;

public enum PluginMessagePath {

    PAY("lighteconomy:pay"),
    SEND_MESSAGE("lighteconomy:message"),
    ;

    private String type;
    PluginMessagePath(String type) { this.type = type; }
    public String getType() {

        return type;
    }
}
