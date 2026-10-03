package com.order.platform.assester.controllers;

import com.order.platform.assester.logging.annotation.Audited;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author: user,
 * date: 23.09.2026
 */

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

    @Audited
    @GetMapping
    public String getHomePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            log.debug("Home page requested by authenticated user {}", userDetails.getUsername());
        } else {
            log.debug("Home page requested by anonymous visitor");
        }
        return "home";
    }
}