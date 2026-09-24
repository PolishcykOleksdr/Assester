package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.LoginUserDTO;
import com.order.platform.assester.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDTO.email(),
                        loginUserDTO.password()
                )
        );

        UserDetails user = (UserDetails) auth.getPrincipal();

        // TODO: finish JWT authentication, save token somewhere (in http),(JwtService, Filter, )
        jwtService.generateToken(user);

        return "redirect:/";
    }
}