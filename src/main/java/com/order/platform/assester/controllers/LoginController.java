package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.LoginUserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * author: user,
 * date: 23.09.2026
 */

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @Valid @ModelAttribute LoginUserDTO loginUserDTO,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if(bindingResult.hasErrors()){
            return getLoginPage();
        }

        if(authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
        ){
            return "redirect:/";
        }

        // TODO: Implement user login logic (especially JWT token generation)

        return "redirect:/";
    }
}