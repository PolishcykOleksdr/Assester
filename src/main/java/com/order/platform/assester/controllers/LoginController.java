package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.LoginUserDTO;
import com.order.platform.assester.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * author: user,
 * date: 23.09.2026
 */

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @GetMapping
    public String getLoginPage(Model model, Authentication authentication) {
        if(authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
        ){
            return "redirect:/";
        }
        model.addAttribute("loginUserDTO", new LoginUserDTO("", ""));
        return "login";
    }

    @PostMapping
    public String loginUser(
            @Valid @ModelAttribute("loginUserDTO") LoginUserDTO loginUserDTO,
            BindingResult bindingResult,
            Authentication authentication,
            HttpServletResponse response
    ) {
        if(bindingResult.hasErrors()){
            return "login";
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUserDTO.getEmail(),
                            loginUserDTO.getPassword()
                    )
            );

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(user);

            ResponseCookie cookie = ResponseCookie.from("BOOK_STORE_TOKEN", token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return "redirect:/";
        } catch (AuthenticationException e) {
            bindingResult.reject("login.error", "Invalid email or password");
            return "login";
        }
    }
}