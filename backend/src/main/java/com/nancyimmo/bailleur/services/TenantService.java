package com.nancyimmo.bailleur.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.dto.TenantDto;
import com.nancyimmo.bailleur.models.TenantModel;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final DocumentRepository documentRepository;

    public TenantService(TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            DocumentRepository documentRepository) {
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.documentRepository = documentRepository;
    }

    public TenantDto create(TenantDto dto) {
        return toDto(tenantRepository.save(toEntity(dto)));
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

    @Transactional
    public void delete(Long id) {
        // Détache le locataire des baux (le bien redevient vacant) et des documents.
        leaseRepository.findByTenantId(id).forEach(lease -> {
            lease.setTenant(null);
            leaseRepository.save(lease);
        });
        documentRepository.findByTenantId(id).forEach(doc -> {
            doc.setTenant(null);
            documentRepository.save(doc);
        });
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
