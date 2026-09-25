package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.RegisterUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

/**
 * author: user,
 * date: 23.09.2026
 */

@Controller
@RequestMapping("/register")
public class RegisterController {
    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUserDTO", new RegisterUserDTO("", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registerUserDTO") RegisterUserDTO registerUserDTO
    ) {
        // TODO: Implement user registration logic (especially JWT token generation)
        return "redirect:/";
    }
}