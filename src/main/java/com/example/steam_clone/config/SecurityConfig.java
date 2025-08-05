package com.example.steam_clone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow access to static resources and public pages
                .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**", "/webfonts/**", "/static/**").permitAll()
                .requestMatchers("/store/**", "/", "/home").permitAll()  // Allow store access without login
                // Require authentication for profile and user-specific pages
                .requestMatchers("/profile/**", "/delete-account", "/library/**", "/cart/**").authenticated()
                .anyRequest().permitAll()  // Allow all other requests (change this later if needed)
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")  // This processes the POST request
                .defaultSuccessUrl("/store", true)  // Redirect to store after successful login
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());  // Disable CSRF for now to avoid token issues

        return http.build();
    }
}