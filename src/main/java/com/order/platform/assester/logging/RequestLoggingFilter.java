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
 * Populates the MDC with per-request context so that every log statement emitted while handling a
 * request can be correlated, and emits one structured entry/exit line per HTTP request.
 *
 * <p>This filter is registered with {@link Ordered#HIGHEST_PRECEDENCE}, so it wraps the Spring Security
 * chain. That means {@code SecurityContextHolderFilter} has already cleared the security context by the
 * time the {@code finally} block below runs - therefore the resolved user is taken from the MDC key that
 * {@code JwtAuthFilter} populated during the chain, with the security context only as a fallback.
 *
 *
 * @author: user,
 * date: 02.10.2026
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
        MDC.put(LogKeys.REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(LogKeys.METHOD, request.getMethod());
        MDC.put(LogKeys.URI, request.getRequestURI());
        MDC.put(LogKeys.REMOTE_ADDR, remoteAddress(request));

        log.debug("Request started");
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.put(LogKeys.USER, currentUserName());
            log.info("Request completed with status {} in {} ms",
                    response.getStatus(),
                    System.currentTimeMillis() - startedAt);
            MDC.clear();
        }
    }

    private String currentUserName() {
        String fromMdc = MDC.get(LogKeys.USER);
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