package com.order.platform.assester.controllers;

import com.order.platform.assester.logging.annotation.Audited;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author: user,
 * date: 23.09.2026
 */

@Controller
@RequestMapping("/")
public class HomeController {

    @Audited
    @GetMapping
    public String getHomePage() {
        return "home";
    }
}
