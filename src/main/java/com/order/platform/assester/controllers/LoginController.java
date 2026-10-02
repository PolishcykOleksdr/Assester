package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.LoginUserDTO;
import com.order.platform.assester.logging.EmailMasker;
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

    @GetMapping
    public String getLoginPage(Model model, Authentication authentication) {
        log.debug("GET /login request received");
        if(authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
        ){
            log.info("Authenticated user '{}' attempted to access login page. Redirecting to home.",
                    EmailMasker.mask(authentication.getName())
            );
            return "redirect:catalog";
        }

        model.addAttribute("loginUserDTO", new LoginUserDTO());
        return "login";
    }

    @PostMapping
    public String loginUser(
            @Valid @ModelAttribute("loginUserDTO") LoginUserDTO loginUserDTO,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        log.debug("POST /login request for email={}", EmailMasker.mask(loginUserDTO.getEmail()));
        if(bindingResult.hasErrors()){
            log.warn("Validation failed during login attempt for email={}. Errors count={}",
                    EmailMasker.mask(loginUserDTO.getEmail()), bindingResult.getErrorCount());
            return "login";
        }

        String cookie = authService.authenticateUser(
                loginUserDTO.getEmail(),
                loginUserDTO.getPassword());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie);

        return "redirect:catalog";
    }
}