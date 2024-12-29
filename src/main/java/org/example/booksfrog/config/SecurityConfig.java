package org.example.booksfrog.config;

import org.example.booksfrog.filter.JwtAuthenticationFilter;
import org.example.booksfrog.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, 
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disabling CSRF as this is a stateless REST API using JWT for authentication,
        // and CSRF tokens are unnecessary in this context.
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors() // Enable CORS
            .and()
            .authorizeHttpRequests(auth -> auth
                // Authentication APIs
                .requestMatchers("/api/auth/**").permitAll()
                // Categories might be public or authenticated—here we allow all
                .requestMatchers("/api/categories/**").permitAll()
                // Let’s allow retrieving book content publicly OR only if authenticated:
                // If you want only logged-in users to see PDFs, switch this to .authenticated().
                .requestMatchers("/api/books/*/content").permitAll()
                
                // You had this example to require auth for recalc:
                .requestMatchers("/api/books/recalculate-total-pages").authenticated()
                
                // Everything else must be authenticated
                .anyRequest().authenticated()
            )
            // Add the JWT filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
