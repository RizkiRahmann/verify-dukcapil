package com.dukcapil.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints untuk health check dan docs
                .requestMatchers("/api/dukcapil/health").permitAll()
                .requestMatchers("/api/dukcapil/ping").permitAll()
                .requestMatchers("/api/dukcapil/docs").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Allow OPTIONS untuk CORS preflight
                .requestMatchers("OPTIONS", "/**").permitAll()
                
                // Semua endpoint dukcapil lainnya untuk development (di production pakai API Key)
                .requestMatchers("/api/dukcapil/**").permitAll()
                
                // Protected by default
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .addHeaderWriter((request, response) -> {
                    response.setHeader("X-Frame-Options", "DENY");
                    response.setHeader("X-Content-Type-Options", "nosniff");
                    response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                    response.setHeader("X-XSS-Protection", "1; mode=block");
                    response.setHeader("X-Service", "Dukcapil-KTP-Service");
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                })
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow Customer Service dan Frontend
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:8080",  // Customer Service
            "http://localhost:3000",  // Frontend React
            "http://localhost:5173",  // Frontend Vite
            "https://*.trycloudflare.com", // Cloudflare tunnels
            "http://127.0.0.1:*"      // Local testing
        ));
        
        // Allow semua HTTP methods yang dibutuhkan
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // Allow headers yang dibutuhkan
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "X-API-Key",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Expose headers untuk response
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "X-Service",
            "X-Total-Count"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}