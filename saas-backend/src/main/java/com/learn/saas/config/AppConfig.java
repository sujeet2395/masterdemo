package com.learn.saas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class AppConfig {

    // ✅ Step 1: Define PasswordEncoder as a Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
            .findAndAddModules() // Auto-loads JavaTimeModule etc.
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();
    }
}