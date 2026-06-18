package com.learn.saas.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TenantDto {
    private Long id;
    private String name;
    private String domain;
    private OrganizationDto organization;
}
