package com.example.ut;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.ut.security.Role;

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    private static final String ROLE_USER = Role.USER.toString();
    private static final String ROLE_MANAGER = Role.MANAGER.toString();
    private static final String ROLE_ADMIN = Role.ADMIN.toString();

    private static final String METHOD_GET = HttpMethod.GET.toString();
    private static final String METHOD_POST = HttpMethod.POST.toString();

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService(final PasswordEncoder passwordEncoder) {
        final UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder.encode("user1"))
                .roles(ROLE_USER)
                .build();

        final UserDetails user2 = User.withUsername("user2")
                .password(passwordEncoder.encode("user2"))
                .roles(ROLE_USER)
                .build();

        final UserDetails manager = User.withUsername("manager")
                .password(passwordEncoder.encode("manager"))
                .roles(ROLE_MANAGER)
                .build();

        final UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(Role.ADMIN.toString())
                .build();

        return new InMemoryUserDetailsManager(user1, user2, manager, admin);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        new AntPathRequestMatcher("/static/**", METHOD_GET),
                                        new AntPathRequestMatcher("/register", METHOD_GET),
                                        new AntPathRequestMatcher("/login", METHOD_GET),
                                        new AntPathRequestMatcher("/authenticate", METHOD_POST),
                                        new AntPathRequestMatcher("/logout", METHOD_POST)).permitAll()
                                .requestMatchers(
                                        new AntPathRequestMatcher("/reward/**")).hasRole(ROLE_USER)
                                .requestMatchers(
                                        new AntPathRequestMatcher("/admin/**")).hasRole(ROLE_ADMIN)
                                .requestMatchers(
                                        new AntPathRequestMatcher("/dashboard/**")).hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)
                                .anyRequest().authenticated()
                )
                .formLogin(login ->
                        login.loginPage("/login")
                                .loginProcessingUrl("/authenticate")
                                .defaultSuccessUrl("/index", false)
                                .failureUrl("/login?error")
                )
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .clearAuthentication(true)
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .build();
    }
}
