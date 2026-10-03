package com.order.platform.assester.handler;

import jakarta.servlet.http.HttpServletRequest;
import com.order.platform.assester.logging.LogKeys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * author: user,
 * date: 03.10.2026
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model, HttpServletRequest request) {
        log.warn("Access denied on {} {} from user={}: {}",
                request.getMethod(), request.getRequestURI(),
                MDC.get(LogKeys.USER.getKey()), ex.getMessage());
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(value = Exception.class)
    public String handleUnexpected(Exception ex, Model model, HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        return "error";
    }
}