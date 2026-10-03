package com.order.platform.assester.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * author: user,
 * date: 03.10.2026
 */

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();
        MDC.put(LogKeys.REQUEST_ID.getKey(), UUID.randomUUID().toString());
        MDC.put(LogKeys.METHOD.getKey(), request.getMethod());
        MDC.put(LogKeys.URI.getKey(), request.getRequestURI());
        MDC.put(LogKeys.REMOTE_ADDR.getKey(), remoteAddress(request));

        log.debug("Request started");
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.put(LogKeys.USER.getKey(), currentUserName());
            log.info("Request completed with status {} in {} ms",
                    response.getStatus(),
                    System.currentTimeMillis() - startedAt);
            MDC.clear();
        }
    }

    private String currentUserName() {
        String fromMdc = MDC.get(LogKeys.USER.getKey());
        if (fromMdc != null && !fromMdc.isBlank()) {
            return fromMdc;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "anonymous";
        }
        return EmailMasker.mask(authentication.getName());
    }

    private String remoteAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}