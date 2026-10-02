package com.order.platform.assester.controllers;

import com.order.platform.assester.logging.EmailMasker;
import com.order.platform.assester.logging.annotation.Audited;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author: user,
 * date: 02.10.2026
 */

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Audited
    @GetMapping
    public String getAdminPage(Authentication authentication) {
        String user = authentication == null ? "anonymous" : authentication.getName();
        log.info("Privileged admin area accessed by {}", EmailMasker.mask(user));
        return "admin";
    }
}