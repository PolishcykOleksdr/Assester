package com.order.platform.assester.handler;

import com.order.platform.assester.exceptions.material.InvalidMaterialException;
import com.order.platform.assester.exceptions.material.MaterialNotFoundException;
import com.order.platform.assester.exceptions.material.MaterialStorageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MaterialExceptionHandler {
    @ExceptionHandler(MaterialNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(MaterialNotFoundException ex, Model model, HttpServletRequest request) {
        return render(HttpStatus.NOT_FOUND, "The requested material or file was not found.", model, request, ex);
    }

    @ExceptionHandler(InvalidMaterialException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalid(InvalidMaterialException ex, Model model, HttpServletRequest request) {
        return render(HttpStatus.BAD_REQUEST, ex.getMessage(), model, request, ex);
    }

    @ExceptionHandler(MaterialStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleStorageFailure(MaterialStorageException ex, Model model, HttpServletRequest request) {
        log.error("Unable to store material files on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return render(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to store uploaded files.", model, request, ex);
    }

    private String render(HttpStatus status, String message, Model model, HttpServletRequest request, Exception ex) {
        log.warn("Material request failed on {} {} with {}", request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName());
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("message", message);
        return "error";
    }
}
