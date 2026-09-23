package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.RegisterUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author: user,
 * date: 23.09.2026
 */

@Controller
@RequestMapping("/register")
public class RegisterController {
    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @GetMapping("/register")
    public String registerUser(
            @ModelAttribute RegisterUserDTO registerUserDTO
    ) {
        // TODO: Implement user registration logic (especially JWT token generation)
        return "redirect:/";
    }
}