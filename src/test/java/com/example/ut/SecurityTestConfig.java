package com.example.ut;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.example.ut.security.Role;

@TestConfiguration(proxyBeanMethods = false)
public class SecurityTestConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService(final PasswordEncoder passwordEncoder) {
        final UserDetails user1 = User.withUsername("test1")
                .password(passwordEncoder.encode("test1"))
                .roles(Role.USER.toString())
                .build();

        final UserDetails user2 = User.withUsername("test2")
                .password(passwordEncoder.encode("test2"))
                .roles(Role.USER.toString())
                .build();

        final UserDetails manager = User.withUsername("manager")
                .password(passwordEncoder.encode("manager"))
                .roles(Role.MANAGER.toString())
                .build();

        final UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(Role.ADMIN.toString())
                .build();

        return new InMemoryUserDetailsManager(user1, user2, manager, admin);
    }
}
