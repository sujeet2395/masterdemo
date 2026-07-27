package com.learn.saas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @CacheEvict(cacheNames = "usersByTenant", key = "#tenantId", condition = "#tenantId != null")
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
    @CacheEvict(cacheNames = "usersByTenant", key = "#tenantId")
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
    @Cacheable(cacheNames = "usersByTenant", key = "#tenantId")
    public List<User> getUsersByTenant(Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "usersByTenant", key = "#result.tenant.id", condition = "#result != null && #result.tenant != null")
    })
    public User updateUser(Long id, String email, String role) {
        return userRepository.findById(id).map(user -> {
            if (email != null && !email.isBlank()) {
                user.setEmail(email);
            }
            if (role != null && !role.isBlank()) {
                user.setRole(role);
            }
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Get a user by username
     */
    @Cacheable(cacheNames = "userByUsername", key = "#username")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
