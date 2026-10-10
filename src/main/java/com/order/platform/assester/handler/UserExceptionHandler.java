package com.order.platform.assester.handler;

import com.order.platform.assester.exceptions.UserAlreadyExistsException;
import com.order.platform.assester.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExists(Exception ex, Model model, HttpServletRequest request) {
        log.warn("Registration conflict on {} {}",
                request.getMethod(), request.getRequestURI());
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("error", HttpStatus.CONFLICT.getReasonPhrase());
        model.addAttribute("message", "An account with these details already exists.");
        return "error";
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model, HttpServletRequest request) {
        log.warn("User not found on {} {}",
                request.getMethod(), request.getRequestURI());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "The requested user was not found.");
        return "error";
    }
}
