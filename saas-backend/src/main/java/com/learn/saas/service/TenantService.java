package com.learn.saas.service;

import com.learn.saas.entity.Tenant;
import com.learn.saas.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    public Optional<Tenant> updateTenant(Long id, Tenant tenantDetails) {
        return tenantRepository.findById(id).map(tenant -> {
            tenant.setName(tenantDetails.getName());
            tenant.setDomain(tenantDetails.getDomain());
            tenant.setOrganization(tenantDetails.getOrganization());
            return tenantRepository.save(tenant);
        });
    }

    public boolean deleteTenant(Long id) {
        if (tenantRepository.existsById(id)) {
            tenantRepository.deleteById(id);
            return true;
        }
        return false;
    }
}