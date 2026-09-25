package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.LoginUserDTO;
import com.order.platform.assester.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("loginUserDTO", new LoginUserDTO("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @Valid @ModelAttribute("loginUserDTO") LoginUserDTO loginUserDTO,
            BindingResult bindingResult,
            Authentication authentication,
            HttpServletResponse response
    ) {
        if(bindingResult.hasErrors()){
            return "login";
        }

        if(authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
        ){
            return "redirect:/";
        }

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDTO.email(),
                        loginUserDTO.password()
                )
        );

        UserDetails user = (UserDetails) auth.getPrincipal();

        String token = jwtService.generateToken(user);
        Cookie cookie = new Cookie("BOOK_STORE_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return "redirect:/";
    }
}