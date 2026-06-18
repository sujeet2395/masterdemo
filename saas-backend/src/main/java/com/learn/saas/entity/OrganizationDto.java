package com.learn.saas.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class OrganizationDto {
    private Long id;
    private String name;
}
