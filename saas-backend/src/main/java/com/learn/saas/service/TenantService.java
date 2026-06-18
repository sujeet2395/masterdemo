package com.learn.saas.service;

import com.learn.saas.entity.Tenant;
import com.learn.saas.entity.TenantDto;
import com.learn.saas.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    private final ModelMapper modelMapper;

    @Cacheable(cacheNames = "allTenants")
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Cacheable(cacheNames = "tenantById", key = "#id")
    public Optional<TenantDto> getTenantById(Long id) {
        Optional<Tenant> tenant = tenantRepository.findById(id);
        if(tenant.isPresent())
            return Optional.of(modelMapper.map(tenant, TenantDto.class));
        return Optional.empty();
    }

    @CacheEvict(cacheNames = {"allTenants", "tenantById"}, allEntries = true)
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @CacheEvict(cacheNames = {"allTenants", "tenantById"}, allEntries = true)
    public Optional<Tenant> updateTenant(Long id, Tenant tenantDetails) {
        return tenantRepository.findById(id).map(tenant -> {
            tenant.setName(tenantDetails.getName());
            tenant.setDomain(tenantDetails.getDomain());
            tenant.setOrganization(tenantDetails.getOrganization());
            return tenantRepository.save(tenant);
        });
    }

    @CacheEvict(cacheNames = {"allTenants", "tenantById"}, allEntries = true)
    public boolean deleteTenant(Long id) {
        if (tenantRepository.existsById(id)) {
            tenantRepository.deleteById(id);
            return true;
        }
        return false;
    }
}