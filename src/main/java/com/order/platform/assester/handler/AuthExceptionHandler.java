package com.order.platform.assester.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * author: user,
 * date: 02.10.2026
 */

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuthExceptionHandler {
    @ExceptionHandler(exception = AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthExc(Exception ex, Model model, HttpServletRequest request) {
        log.warn("Authentication failure on {} {}: {}",
                request.getMethod(), request.getRequestURI(),
                ex.getClass().getSimpleName());
        model.addAttribute("status", HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        model.addAttribute("message", "Authentication failed. Please check your credentials and try again.");
        return "error";
    }
}
