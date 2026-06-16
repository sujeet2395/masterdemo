package com.learn.saas.entity;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Tenant", description = "Tenant/Organization instance for multi-tenancy")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique tenant identifier", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Tenant name", example = "Acme Corporation", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column
    @Schema(description = "Tenant subdomain/domain", example = "acme.example.com")
    private String domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    @JsonBackReference
    @Schema(hidden = true)
    private Organization organization;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(hidden = true)
    private List<User> users;
}