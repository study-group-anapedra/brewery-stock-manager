package com.anapedra.stock_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ActuatorSecurityConfig {

    @Bean
    @Order(1) // Este filter chain roda antes dos demais
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/actuator/**") // ⬅️ Restrito SOMENTE ao actuator
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ⬅️ libera todos os endpoints actuator
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
