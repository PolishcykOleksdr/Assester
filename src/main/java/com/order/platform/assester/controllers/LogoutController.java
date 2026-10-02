package com.order.platform.assester.controllers;

import com.order.platform.assester.logging.EmailMasker;
import com.order.platform.assester.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author: user,
 * date: 29.09.2026
 */

@Controller
@RequestMapping("/logout")
@RequiredArgsConstructor
@Slf4j
public class LogoutController {
    private final AuthService authService;

    @PostMapping
    public String logout(HttpServletResponse response, Authentication authentication) {
        String user = isAuthenticated(authentication)
                ? EmailMasker.mask(authentication.getName())
                : "anonymous";

        log.debug("POST /logout request for user={}", user);
        String expiredCookie = authService.logoutUser();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie);

        log.info("User {} logged out successfully", user);
        return "redirect:/";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}