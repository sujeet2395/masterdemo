package com.learn.saas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.learn.saas.entity.User;
import com.learn.saas.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.security.user.username}")
    private String superAdminUsername;

    @Value("${spring.security.user.email}")
    private String superAdminEmail;

    @Value("${spring.security.user.password}")
    private String superAdminPassword;

    @Value("${spring.security.user.role}")
    private String superAdminRole;

    @Override
    public void run(String... args) throws Exception {
        // Create SuperAdmin user from application.properties if it doesn't exist
        if (userRepository.findByUsername(superAdminUsername).isEmpty()) {
            User superAdmin = User.builder()
                    .username(superAdminUsername)
                    .email(superAdminEmail)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .role(superAdminRole)
                    .tenant(null)  // SuperAdmin is not tied to any tenant
                    .build();
            
            userRepository.save(superAdmin);
            System.out.println("✅ SuperAdmin user created: " + superAdminUsername);
        } else {
            System.out.println("✅ SuperAdmin user already exists: " + superAdminUsername);
        }
    }
}
