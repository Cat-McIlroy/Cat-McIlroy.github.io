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

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> {
                csrf.disable();
            })
            .authorizeHttpRequests(auth -> auth
                // public endpoints
                .requestMatchers(
                    "/index.html",
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
                    // "/drugs/**",
                    "/pharmacies/sign-out",
                    "/pharmacy-drug-availabilities/create",
                    "/pharmacy-drug-availabilities/view-all",
                    "/pharmacy-drug-availabilities/update/{id}",
                    "/pharmacy-drug-availabilities/delete/{id}"
                    // "/pharmacy-drug-availabilities/**"
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
                    // redirect to home page
                    response.sendRedirect("/index.html");
                }
            })
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID"));

        return http.build();
    }

}
