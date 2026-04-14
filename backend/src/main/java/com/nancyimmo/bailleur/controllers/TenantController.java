package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.nancyimmo.bailleur.dto.TenantDto;
import com.nancyimmo.bailleur.services.TenantService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("")
    public List<TenantDto> getAllTenants() {
        return tenantService.getAll();
    }

    @GetMapping("/{id}")
    public TenantDto findById(@PathVariable Long id) {
        return tenantService.findById(id);
    }

    @PutMapping("/{id}")
    public TenantDto updateTenant(@PathVariable Long id, @RequestBody TenantDto tenantDto) {
        return tenantService.update(id, tenantDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTenant(@PathVariable Long id) {
        tenantService.delete(id);
    }

}
