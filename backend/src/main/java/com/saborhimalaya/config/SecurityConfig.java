package com.saborhimalaya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configuración provisional mientras se implementan la autenticación y los
    // roles.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(autorizacion -> autorizacion
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/platos", "/api/platos/**", "/api/menus", "/api/menus/**")
                        .permitAll()
                        .requestMatchers("/api/pedidos", "/api/pedidos/**").authenticated()
                        .anyRequest().authenticated());
        return http.build();
    }
}
