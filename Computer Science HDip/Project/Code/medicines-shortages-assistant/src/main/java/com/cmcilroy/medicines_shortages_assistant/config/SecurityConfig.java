package com.cmcilroy.medicines_shortages_assistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // allow all origins, methods and headers for duration of development and testing. this would be changed in production
        configuration.addAllowedOrigin("http://localhost:5500");
        configuration.addAllowedOrigin("http://127.0.0.1:5500");
        configuration.addAllowedMethod("*");  
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);  
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> {
                csrf.disable();
            })
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // public endpoints
                .requestMatchers(
                    "/pharmacies/register",
                    "/pharmacies/sign-in",
                    "/pharmacies/check-auth",
                    "/drugs/shortages",
                    "/drugs/search-by-active-substance",
                    "/drugs/search-by-product-name",
                    "/pharmacy-drug-availabilities/search-for-stock"
                ).permitAll()
                // endpoints restricted to authenticated pharmacy users only
                .requestMatchers(
                    "/pharmacies/sign-out",
                    "/pharmacies/delete-account",
                    "/pharmacies/edit-account-details",
                    "/pharmacy-drug-availabilities/create",
                    "/pharmacy-drug-availabilities/view-all",
                    "/pharmacy-drug-availabilities/delete/{id}"
                ).hasRole("PHARMACY_USER")
            )
            // enable session-based authentication to persist authentication across requests
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // disable default login page
            .formLogin(form -> form.disable())

            .logout(logout -> logout
            // sign-out endpoint
            .logoutUrl("/pharmacies/sign-out")
            .logoutSuccessHandler((request, response, authentication) -> {
                // if authentication is null or not authenticated
                if (authentication == null || !authentication.isAuthenticated()) {
                    // return 403 Forbidden
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                } else {
                    // sign out the user
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Sign-out successful\"}");
                    response.getWriter().flush();
                }
            })
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID"));

        return http.build();
    }

}
