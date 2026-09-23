package com.order.platform.assester.services;

import com.order.platform.assester.entities.User;
import com.order.platform.assester.exceptions.UserNotFoundException;
import com.order.platform.assester.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * author: user,
 * date: 23.09.2026
 */

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with email %s not found", email)
                ));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}