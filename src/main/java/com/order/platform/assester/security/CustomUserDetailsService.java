package com.order.platform.assester.security;

import com.order.platform.assester.entities.User;
import com.order.platform.assester.enums.Role;
import com.order.platform.assester.logging.EmailMasker;
import com.order.platform.assester.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: user,
 * date: 24.09.2026
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User details lookup failed: no user with email {}", EmailMasker.mask(email));
                    return new UsernameNotFoundException("Invalid credentials");
                });

        Role role = user.getRole() == null ? Role.USER : user.getRole();
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(role.getRoleName())
        );

        log.debug("Loaded user details for {} with authorities {}", EmailMasker.mask(email), authorities);

        return CustomUserDetails.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
