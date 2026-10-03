package com.order.platform.assester.security;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.order.platform.assester.logging.EmailMasker;
import com.order.platform.assester.logging.LogKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * author: user,
 * date: 24.09.2026
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.token-name}")
    private String authTokenName;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String jwt = getToken(request);

        if (jwt == null) {
            log.debug("No auth token presented for {} {}",
                    request.getMethod(), request.getRequestURI());
        } else if (!jwtService.validateToken(jwt)) {
            log.warn("Rejected invalid or expired JWT presented via {} for {} {} from {}",
                    tokenSource(request), request.getMethod(), request.getRequestURI(),
                    MDC.get(LogKeys.REMOTE_ADDR.getKey()));
        } else {
            String email = jwtService.getEmailFromToken(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    if (userDetails.isEnabled()) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        MDC.put(LogKeys.USER.getKey(), EmailMasker.mask(email));
                        log.info("JWT accepted for user {} with authorities {} from {}",
                                EmailMasker.mask(email), userDetails.getAuthorities(),
                                MDC.get(LogKeys.REMOTE_ADDR.getKey()));
                    } else {
                        log.warn("Account {} is disabled - authentication rejected", EmailMasker.mask(email));
                    }
                } catch (UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();
                    log.warn("JWT subject {} no longer exists - authentication rejected",
                            EmailMasker.mask(email));
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String tokenSource(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            return "Authorization header";
        }
        return "cookie " + authTokenName;
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> authTokenName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
