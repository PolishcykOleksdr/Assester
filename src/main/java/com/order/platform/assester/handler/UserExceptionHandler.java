package com.order.platform.assester.handler;

import com.order.platform.assester.exceptions.UserAlreadyExistsException;
import com.order.platform.assester.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * author: user,
 * date: 02.10.2026
 */

@Slf4j
@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public String handleAlreadyExists(Exception ex, Model model, HttpServletRequest request) {
        log.warn("Registration conflict on {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("error", HttpStatus.CONFLICT.getReasonPhrase());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public String handleNotFound(Exception ex, Model model, HttpServletRequest request) {
        log.warn("User not found on {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}