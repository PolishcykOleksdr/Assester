package com.order.platform.assester.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * author: user,
 * date: 23.09.2026
 */

public record RegisterUserDTO(
        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotBlank(message = "Last name cannot be empty")
        String lastName,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be empty")
        String email
) {
}
