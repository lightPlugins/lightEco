package io.lightstudio.economy.messaging.util;

public enum SubChannelPath {

    PAY("lighteconomy:pay"),
    SEND_MESSAGE("lighteconomy:message"),
    UPDATE_BALANCE("lighteconomy:updatebalance"),
    ;

    private String type;
    SubChannelPath(String type) { this.type = type; }
    public String getId() {

        return type;
    }
}
