package com.order.platform.assester.services;

import com.order.platform.assester.dto.RegisterUserDTO;
import com.order.platform.assester.entities.User;
import com.order.platform.assester.exceptions.UserNotFoundException;
import com.order.platform.assester.mapper.UserMapper;
import com.order.platform.assester.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * author: user,
 * date: 23.09.2026
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with email %s not found", email)
                ));
    }

    public Long createUser(RegisterUserDTO registerUserDTO) {
        if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = userMapper.toEntity(registerUserDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("User with id {} has been created", user.getId());
        return userRepository.save(user).getId();
    }
}