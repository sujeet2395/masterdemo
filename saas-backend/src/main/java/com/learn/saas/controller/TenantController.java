package com.learn.saas.controller;

import com.learn.saas.entity.Tenant;
import com.learn.saas.entity.TenantDto;
import com.learn.saas.service.TenantService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenant Management", description = "Tenant/Organization management endpoints - multi-tenant isolation")
@SecurityRequirement(name = "Bearer Authentication")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    @Operation(
        summary = "Get All Tenants",
        description = "Retrieve all tenants in the system (Admin access required)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of all tenants",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tenant.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        )
    })
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get Tenant by ID",
        description = "Retrieve a specific tenant by its identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tenant found - returns tenant details"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found - tenant does not exist"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing JWT token"
        )
    })
    public ResponseEntity<TenantDto> getTenantById(
            @Parameter(description = "Tenant ID", example = "1")
            @PathVariable Long id) {
        return tenantService.getTenantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Create New Tenant",
        description = "Create a new tenant/organization (SuperAdmin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tenant created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tenant.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid tenant data"
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
    public Tenant createTenant(@RequestBody Tenant tenant) {
        return tenantService.createTenant(tenant);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update Tenant",
        description = "Update an existing tenant's details (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tenant updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found - tenant does not exist"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid tenant data"
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
    public ResponseEntity<Tenant> updateTenant(
            @Parameter(description = "Tenant ID", example = "1")
            @PathVariable Long id,
            @RequestBody Tenant tenant) {
        return tenantService.updateTenant(id, tenant)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete Tenant",
        description = "Delete a tenant and all associated data (SuperAdmin only - use with caution)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Tenant deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found - tenant does not exist"
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
    public ResponseEntity<Void> deleteTenant(
            @Parameter(description = "Tenant ID to delete", example = "1")
            @PathVariable Long id) {
        if (tenantService.deleteTenant(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}