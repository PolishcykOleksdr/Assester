package com.order.platform.assester.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: user,
 * date: 23.09.2026
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {
        @NotBlank(message = "Username cannot be empty")
        private String username;

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be empty")
        private String email;

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        private String password;
}
