package com.learn.saas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for SaaS Backend
 * 
 * Configures Springdoc-OpenAPI to generate OpenAPI 3.0 specification
 * with JWT Bearer token authentication.
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access OpenAPI JSON at: http://localhost:8080/v3/api-docs
 * 
 * ## How to Use:
 * 1. Start the application: mvn spring-boot:run
 * 2. Open browser: http://localhost:8080/swagger-ui.html
 * 3. Call POST /api/auth/login with username & password
 * 4. Copy the returned JWT token
 * 5. Click "Authorize" button at top-right
 * 6. Paste token as "Bearer <token>"
 * 7. Click Authorize and test protected endpoints
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SaaS Backend API")
                        .version("1.0.0")
                        .description("Multi-tenant SaaS Backend Platform\n\n" +
                                "## Quick Start\n" +
                                "1. **Login**: POST `/api/auth/login` with username & password\n" +
                                "2. **Get Token**: Copy the JWT token from response\n" +
                                "3. **Authorize**: Click 'Authorize' button, enter 'Bearer <token>'\n" +
                                "4. **Test APIs**: Use protected endpoints with automatic auth\n\n" +
                                "## Features\n" +
                                "- Multi-tenant isolation (Organization-based)\n" +
                                "- Role-based access control (SUPER_ADMIN, ADMIN, USER)\n" +
                                "- User authentication & authorization (JWT)\n" +
                                "- User account management\n" +
                                "- Tenant management\n" +
                                "- Audit logging\n\n" +
                                "## Authentication\n" +
                                "Protected endpoints require Bearer token:\n" +
                                "`Authorization: Bearer <your_jwt_token>`\n\n" +
                                "## Roles & Permissions\n" +
                                "- **SUPER_ADMIN**: Full platform access\n" +
                                "- **ADMIN**: Organization admin access\n" +
                                "- **USER**: Basic user access\n\n" +
                                "## Error Codes\n" +
                                "- `401 Unauthorized`: Missing or invalid token\n" +
                                "- `403 Forbidden`: Insufficient permissions\n" +
                                "- `400 Bad Request`: Invalid request\n" +
                                "- `404 Not Found`: Resource not found\n")
                        .contact(new Contact()
                                .name("Backend Team")
                                .url("https://docs.company.com")
                                .email("support@company.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                // Define security scheme
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token for authentication\n\n" +
                                                "**Format**: Bearer <jwt_token>\n\n" +
                                                "**Example**: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n\n" +
                                                "**How to get token**:\n" +
                                                "1. Call POST /api/auth/login\n" +
                                                "2. Use credentials for your user account\n" +
                                                "3. Copy token from response\n" +
                                                "4. Paste here in 'Bearer <token>' format")))
                
                // Apply security globally (can be overridden per endpoint)
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"));
    }
}
