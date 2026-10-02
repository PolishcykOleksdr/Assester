package com.order.platform.assester.services;

import com.order.platform.assester.logging.EmailMasker;
import com.order.platform.assester.logging.LogKeys;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * author: user,
 * date: 28.09.2026
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    @Value("${jwt.token-name}")
    private String tokenName;
    @Value("${jwt.secure-cookie:true}")
    private boolean secureCookie;

    @Audited
    public String authenticateUser(String email, String password) throws AuthenticationException {
        Authentication auth;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );
        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email {} from {} [{}]",
                    EmailMasker.mask(email), MDC.get(LogKeys.REMOTE_ADDR), e.getClass().getSimpleName());
            throw e;
        }

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from(tokenName, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        log.debug("Auth cookie issued for user {} (name={}, secure={}, maxAge={}s)",
                EmailMasker.mask(user.getUsername()), tokenName, secureCookie, 24 * 60 * 60);
        log.info("Authentication successful for user: {}", EmailMasker.mask(user.getUsername()));
        return cookie.toString();
    }

    public String logoutUser() throws AuthenticationException {
        ResponseCookie cookie = ResponseCookie.from(tokenName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return cookie.toString();
    }
}