package com.order.platform.assester.services;

import com.order.platform.assester.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public String authenticateUser(String email, String password) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from(tokenName, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        log.info("Authentication successful for user: {}", user.getUsername());
        return cookie.toString();
    }

    public String logoutUser() {
        ResponseCookie cookie = ResponseCookie.from(tokenName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        log.info("Cleaned auth cookie for logout");
        return cookie.toString();
    }
}