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
    name = "AuthResponse",
    description = "Authentication response containing JWT token",
    example = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
)
public class AuthResponse {
    
    @Schema(
        description = "JWT Bearer token for authentication (valid for 15 minutes). Use in Authorization header: 'Bearer <token>'",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTcyMzc2NTQ0MCwiaWF0IjoxNzIzNjc4MDQwfQ...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;
}