package com.order.platform.assester.exceptions;

/**
 * author: user,
 * date: 23.09.2026
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
