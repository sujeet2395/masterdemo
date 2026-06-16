package com.learn.saas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.learn.saas.exception.AuthEntryPointExceptionHandler;

import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthEntryPointExceptionHandler authEntryPointExceptionHandler;

    // ✅ Step 3: Configure UserDetailsService to load users from database
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    // ✅ Step 4: Configure HTTP Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authEntryPointExceptionHandler)
                .accessDeniedHandler(authEntryPointExceptionHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // Allow Swagger/OpenAPI endpoints
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Auth endpoints (no authentication required)
                .requestMatchers("/api/auth/login").permitAll()  // open endpoints
                .requestMatchers("/api/auth/refresh").permitAll()  // refresh token
                .requestMatchers("/api/auth/encode-password").permitAll() // encode password of existed user
                .requestMatchers("/public/**").permitAll()  // open endpoints
                // Admin-only endpoints
                .requestMatchers("/api/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN") // admin-only endpoints
                .requestMatchers("/api/tenants/**").hasAnyRole("SUPER_ADMIN", "ADMIN") // admin-only endpoints
                .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN") // admin-only endpoints
                .anyRequest().authenticated()               // lock everything else
            )
            .httpBasic(Customizer.withDefaults());          // or formLogin()

        return http.build();
    }
}
