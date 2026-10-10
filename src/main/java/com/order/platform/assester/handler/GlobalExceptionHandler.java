package com.order.platform.assester.handler;

import jakarta.servlet.http.HttpServletRequest;
import com.order.platform.assester.logging.LogKeys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * author: user,
 * date: 03.10.2026
 */

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model, HttpServletRequest request) {
        log.warn("Access denied on {} {} from user={} ({})",
                request.getMethod(), request.getRequestURI(),
                MDC.get(LogKeys.USER.getKey()), ex.getClass().getSimpleName());
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        model.addAttribute("message", "You do not have permission to access this page.");
        return "error";
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception ex, Model model, HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", "An unexpected error occurred. Please try again later.");
        return "error";
    }

    @ExceptionHandler(ResponseStatusException.class)
    public String handleExpectedStatus(ResponseStatusException ex, Model model,
                                       HttpServletRequest request,
                                       jakarta.servlet.http.HttpServletResponse response) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        response.setStatus(status.value());
        log.warn("Request {} {} rejected with status {}", request.getMethod(), request.getRequestURI(), status.value());
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("message", ex.getReason() == null ? status.getReasonPhrase() : ex.getReason());
        return "error";
    }
}
