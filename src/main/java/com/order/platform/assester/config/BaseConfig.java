package com.order.platform.assester.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * author: user,
 * date: 23.09.2026
 */

@Configuration
public class BaseConfig {
    //TODO: solve the issue with home page after auth login, test everything
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}