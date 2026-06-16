package com.learn.saas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.learn.saas.entity.Tenant;
import com.learn.saas.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void createTenantShouldPersistTenantDetails() {
        Tenant tenantRequest = Tenant.builder()
                .name("Simple Agent")
                .domain("simpleagent.masterdemo.com")
                .build();
        Tenant savedTenant = Tenant.builder()
                .id(1L)
                .name("Simple Agent")
                .domain("simpleagent.masterdemo.com")
                .build();
        when(tenantRepository.save(any(Tenant.class))).thenReturn(savedTenant);

        Tenant createdTenant = tenantService.createTenant(tenantRequest);

        ArgumentCaptor<Tenant> tenantCaptor = ArgumentCaptor.forClass(Tenant.class);
        verify(tenantRepository).save(tenantCaptor.capture());
        assertThat(tenantCaptor.getValue().getName()).isEqualTo("Simple Agent");
        assertThat(tenantCaptor.getValue().getDomain()).isEqualTo("simpleagent.masterdemo.com");
        assertThat(createdTenant.getId()).isEqualTo(1L);
        assertThat(createdTenant.getName()).isEqualTo("Simple Agent");
        assertThat(createdTenant.getDomain()).isEqualTo("simpleagent.masterdemo.com");
    }
}
