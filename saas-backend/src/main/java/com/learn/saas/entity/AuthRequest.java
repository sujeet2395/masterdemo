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
    name = "AuthRequest",
    description = "Authentication request containing user credentials",
    example = "{\"username\": \"admin@example.com\", \"password\": \"SecurePassword123!\"}"
)
public class AuthRequest {
    
    @Schema(
        description = "Username or email address of the user",
        example = "admin@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
    
    @Schema(
        description = "Password for the user account",
        example = "SecurePassword123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}