package com.learn.saas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learn.saas.entity.User;
import com.learn.saas.entity.Tenant;
import com.learn.saas.repository.UserRepository;
import com.learn.saas.repository.TenantRepository;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a user by SuperAdmin (can create any role)
     */
    public User registerBySuperAdmin(String username, String email, String password, String role, Long tenantId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Tenant tenant = null;
        if (tenantId != null) {
            tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role != null ? role : "USER")
                .tenant(tenant)
                .build();

        return userRepository.save(user);
    }

    /**
     * Register a user by OrgAdmin (can only create USER role for their tenant)
     */
    public User registerByOrgAdmin(String username, String email, String password, Long tenantId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role("USER")  // OrgAdmins can only create regular users
                .tenant(tenant)
                .build();

        return userRepository.save(user);
    }

    /**
     * Get all users in a tenant
     */
    public List<User> getUsersByTenant(Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    /**
     * Get a user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
