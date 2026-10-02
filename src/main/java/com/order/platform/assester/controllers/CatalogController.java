package com.order.platform.assester.controllers;

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
@RequestMapping("/catalog")
public class CatalogController {
    @GetMapping
    public String getCatalogPage(Authentication authentication) {
        log.debug("Catalog page requested by {}", authentication.getName());
        return "catalog";
    }
}