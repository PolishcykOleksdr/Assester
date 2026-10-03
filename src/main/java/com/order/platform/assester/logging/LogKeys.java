package com.order.platform.assester.logging;

/**
 * author: user,
 * date: 03.10.2026
 */
public enum LogKeys {

    REQUEST_ID("requestId"),
    USER("user"),
    METHOD("method"),
    URI("uri"),
    REMOTE_ADDR("remoteAddr");

    private String key;

    LogKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}