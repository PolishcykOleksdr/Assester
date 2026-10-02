package com.order.platform.assester.exceptions;

/**
 * author: user,
 * date: 02.10.2026
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
