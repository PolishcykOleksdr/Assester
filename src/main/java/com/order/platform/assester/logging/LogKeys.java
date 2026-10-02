package com.order.platform.assester.logging;

/**
 * MDC keys shared between {@link RequestLoggingFilter} and the logging configuration patterns.
 *
 * @author: user,
 * date: 02.10.2026
 */

public final class LogKeys {

    public static final String REQUEST_ID = "requestId";
    public static final String USER = "user";
    public static final String METHOD = "method";
    public static final String URI = "uri";
    public static final String REMOTE_ADDR = "remoteAddr";

    private LogKeys() {
    }
}