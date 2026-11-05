package com.order.platform.assester.logging;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * author: user,
 * date: 05.11.2026
 */

public final class RequestExclusions {
    private RequestExclusions() {}

    private static final List<String> PUBLIC_PATHS = List.of("/css/", "/js/",
            "/favicon.ico", "/actuator", "/actuator");

    public static boolean shouldExclude(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return PUBLIC_PATHS.stream().anyMatch(uri::startsWith);
    }
}