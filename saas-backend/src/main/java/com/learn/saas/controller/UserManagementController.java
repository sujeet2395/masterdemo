package com.learn.saas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.learn.saas.entity.User;
import com.learn.saas.entity.UserRegistrationRequest;
import com.learn.saas.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User account management endpoints - requires ADMIN or SUPER_ADMIN role")
@SecurityRequirement(name = "Bearer Authentication")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
        summary = "Register Organization Admin",
        description = "SuperAdmin only - Register a new organization admin or user for any tenant",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - missing required fields or tenant ID not found"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - user does not have SUPER_ADMIN role"
        )
    })
    public ResponseEntity<?> registerOrgAdmin(@RequestBody UserRegistrationRequest request) {
        try {
            if (request.getRole() == null || request.getRole().isEmpty()) {
                request.setRole("ADMIN");
            }
            if (request.getTenantId() == null) {
                return ResponseEntity.status(400).body("Error: Tenant ID is required");
            }
            
            User newUser = userService.registerBySuperAdmin(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                request.getTenantId()
            );
            
            return ResponseEntity.ok("User registered successfully: " + newUser.getUsername() + " with role: " + newUser.getRole());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register-user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Register User",
        description = "Admin only - Register a new user within their organization tenant"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully with USER role"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid user data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - user does not have ADMIN role"
        )
    })
    public ResponseEntity<?> registerUser(
            @RequestBody UserRegistrationRequest request,
            @Parameter(description = "Organization Tenant ID", example = "1")
            @RequestParam Long tenantId) {
        try {
            User newUser = userService.registerByOrgAdmin(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                tenantId
            );
            
            return ResponseEntity.ok("User registered successfully: " + newUser.getUsername() + " with role: USER");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(
        summary = "Get Users by Tenant",
        description = "Retrieve all users in a specific tenant (Admin and SuperAdmin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of users in tenant",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - tenant not found"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - user does not have required role"
        )
    })
    public ResponseEntity<?> getUsersByTenant(
            @Parameter(description = "Tenant ID", example = "1")
            @PathVariable Long tenantId) {
        try {
            List<User> users = userService.getUsersByTenant(tenantId);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(
        summary = "Get User by Username",
        description = "Retrieve user details by username (Admin and SuperAdmin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found - returns user details"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found - user not found in system"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - user does not have required role"
        )
    })
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username to search for", example = "admin")
            @PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(
        summary = "Update User",
        description = "Update a user account by admin or superadmin"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid user data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - user does not have required role"
        )
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            @RequestBody UserRegistrationRequest request) {
        try {
            User updatedUser = userService.updateUser(id, request.getEmail(), request.getRole());
            return ResponseEntity.ok("User updated successfully: " + updatedUser.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
}
