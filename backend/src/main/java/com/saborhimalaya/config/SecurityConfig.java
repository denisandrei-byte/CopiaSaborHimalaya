package com.saborhimalaya.config;

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

    // Configuración provisional mientras se implementan la autenticación y los
    // roles.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/platos", "/api/platos/**", "/api/menus", "/api/menus/**"))
                .authorizeHttpRequests(autorizacion -> autorizacion
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/platos", "/api/platos/**", "/api/menus", "/api/menus/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/pedidos").authenticated() // Para autenticacion
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // Para encriptar las contraseñas en la base de datos
        return new BCryptPasswordEncoder();
    }
}