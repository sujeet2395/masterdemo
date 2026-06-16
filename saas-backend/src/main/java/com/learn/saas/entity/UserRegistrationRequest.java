package com.learn.saas.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "UserRegistrationRequest",
    description = "User account registration request",
    example = "{\"username\": \"newuser\", \"email\": \"user@example.com\", \"password\": \"SecurePass123!\", \"role\": \"ADMIN\", \"tenantId\": 1}"
)
public class UserRegistrationRequest {
    
    @Schema(
        description = "Unique username for login",
        example = "newuser",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
    
    @Schema(
        description = "Email address of the user",
        example = "user@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
    
    @Schema(
        description = "Account password (at least 8 characters, 1 uppercase, 1 number, 1 special char)",
        example = "SecurePass123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
    
    @Schema(
        description = "User role (default: USER for regular registrations)",
        example = "ADMIN",
        allowableValues = {"SUPER_ADMIN", "ADMIN", "USER"}
    )
    private String role;
    
    @Schema(
        description = "Tenant/Organization ID the user belongs to",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long tenantId;
}
