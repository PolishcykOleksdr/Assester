package com.order.platform.assester.handler;

import com.order.platform.assester.exceptions.course.CourseNotFoundExcpetion;
import com.order.platform.assester.exceptions.user.UserAlreadyExistsException;
import com.order.platform.assester.exceptions.user.UserNotFoundException;
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
public class CourseExceptionHandler {
    @ExceptionHandler(value = CourseNotFoundExcpetion.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model, HttpServletRequest request) {
        log.warn("Course not found on {} {}",
                request.getMethod(), request.getRequestURI());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "The requested course was not found.");
        return "error";
    }
}
