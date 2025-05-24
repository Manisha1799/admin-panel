package com.admin.config;

import com.admin.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Configuration  // Marks this as a configuration class
@EnableWebSecurity  // Enables Spring Security
@EnableMethodSecurity
@RequiredArgsConstructor  // Creates constructor for final fields
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // Define which URLs are public and which need authentication
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure CORS (Cross-Origin Resource Sharing)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));  // Allow all origins
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));  // Allow common HTTP methods
                    config.setAllowedHeaders(List.of("*"));  // Allow all headers
                    return config;
                }))
                // Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())
                // Configure URL security
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/products/**").hasAuthority("ROLE_USER")
                            .anyRequest().authenticated();
                    log.debug("Security rules configured: /api/products/** requires ROLE_USER");
                })
                // Use stateless session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Add JWT filter before Spring's authentication filter
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configure how Spring Security will authenticate users
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // Service to load user details
        provider.setPasswordEncoder(passwordEncoder());      // Password encoder
        return provider;
    }

    // Required for processing authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Define password encoder for secure password storage
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Create RestTemplate bean for LocationService
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}