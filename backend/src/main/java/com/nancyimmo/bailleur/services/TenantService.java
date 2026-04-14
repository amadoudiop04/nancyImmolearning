package com.nancyimmo.bailleur.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.dto.TenantDto;
import com.nancyimmo.bailleur.models.TenantModel;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantDto create(TenantModel tenant) {
        return toDto(tenantRepository.save(tenant));
    }

    public List<TenantDto> getAll() {
        return tenantRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public TenantDto findById(Long id) {
        return tenantRepository.findById(id).map(this::toDto).orElse(null);
    }

    public TenantDto update(Long id, TenantDto tenantDto) {
        TenantModel tenant = toEntity(tenantDto);
        tenant.setId(id);
        return toDto(tenantRepository.save(tenant));
    }

    public void delete(Long id) {
        tenantRepository.deleteById(id);
    }

    public TenantDto toDto(TenantModel model) {
        return new TenantDto(
                model.getId(),
                model.getFirstName(),
                model.getLastName(),
                model.getEmail(),
            model.getPhone(),
                model.getStreet(),
                model.getCity(),
                model.getZipCode(),
                model.getCountry());
    }

    private TenantModel toEntity(TenantDto dto) {
        TenantModel model = new TenantModel();
        model.setId(dto.getId());
        model.setFirstName(dto.getFirstName());
        model.setLastName(dto.getLastName());
        model.setEmail(dto.getEmail());
        model.setPhone(dto.getPhone());
        model.setStreet(dto.getStreet());
        model.setCity(dto.getCity());
        model.setZipCode(dto.getZipCode());
        model.setCountry(dto.getCountry());
        return model;
    }

}
