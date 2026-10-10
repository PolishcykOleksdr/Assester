package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.RegisterUserDTO;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.services.AuthService;
import com.order.platform.assester.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequiredArgsConstructor
@Slf4j
public class RegisterController {
    private final AuthService authService;
    private final UserService userService;

    @Audited
    @GetMapping
    public String getRegisterPage(Model model, Authentication authentication) {
        if(authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
        ){
            return "redirect:catalog";
        }

        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        return "register";
    }

    @Audited
    @PostMapping
    public String registerUser(
            @Valid @ModelAttribute("registerUserDTO") RegisterUserDTO registerUserDTO,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        if(bindingResult.hasErrors()){
            log.warn("Registration form validation failed; errorCount={}", bindingResult.getErrorCount());
            return "register";
        }

        userService.createUser(registerUserDTO);
        String cookie = authService.authenticateUser(
                registerUserDTO.getEmail(),
                registerUserDTO.getPassword());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie);

        return "redirect:catalog";
    }
}
