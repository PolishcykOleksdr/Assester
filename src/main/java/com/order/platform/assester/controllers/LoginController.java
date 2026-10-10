package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.LoginUserDTO;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * author: user,
 * date: 23.09.2026
 */

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final AuthService authService;

    @Audited
    @GetMapping
    public String getLoginPage(Model model, Authentication authentication) {
        if(authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
        ){
            return "redirect:catalog";
        }

        model.addAttribute("loginUserDTO", new LoginUserDTO());
        return "login";
    }

    @Audited
    @PostMapping
    public String loginUser(
            @Valid @ModelAttribute("loginUserDTO") LoginUserDTO loginUserDTO,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        if(bindingResult.hasErrors()){
            log.warn("Login form validation failed; errorCount={}", bindingResult.getErrorCount());
            return "login";
        }

        String cookie = authService.authenticateUser(
                loginUserDTO.getEmail(),
                loginUserDTO.getPassword());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie);

        return "redirect:catalog";
    }
}
