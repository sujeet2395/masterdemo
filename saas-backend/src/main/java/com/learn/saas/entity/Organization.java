package com.learn.saas.entity;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Organization", description = "Organization entity containing multiple tenants")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique organization identifier", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Organization name", example = "Acme Inc", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column
    @Schema(description = "Organization description", example = "Enterprise SaaS platform")
    private String description;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Schema(description = "List of tenants within this organization")
    private List<Tenant> tenants;
}