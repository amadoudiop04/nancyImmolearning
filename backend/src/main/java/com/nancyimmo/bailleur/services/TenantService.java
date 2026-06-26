package com.nancyimmo.bailleur.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.security.CurrentUser;
import com.nancyimmo.bailleur.dto.TenantDto;
import com.nancyimmo.bailleur.models.TenantModel;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final DocumentRepository documentRepository;
    private final CurrentUser currentUser;

    public TenantService(TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            DocumentRepository documentRepository,
            CurrentUser currentUser) {
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.documentRepository = documentRepository;
        this.currentUser = currentUser;
    }

    public TenantDto create(TenantDto dto) {
        TenantModel model = toEntity(dto);
        // Le locataire appartient au bailleur connecté.
        model.setLandlord(currentUser.requireLandlord());
        return toDto(tenantRepository.save(model));
    }

    public List<TenantDto> getAll() {
        return tenantRepository.findByLandlord_Email(currentUser.requireEmail())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public TenantDto findById(Long id) {
        return tenantRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .map(this::toDto).orElse(null);
    }

    public TenantDto update(Long id, TenantDto tenantDto) {
        // On ne modifie que les locataires du bailleur connecté.
        return tenantRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .map(existing -> {
                    existing.setFirstName(tenantDto.getFirstName());
                    existing.setLastName(tenantDto.getLastName());
                    existing.setEmail(tenantDto.getEmail());
                    existing.setPhone(tenantDto.getPhone());
                    existing.setStreet(tenantDto.getStreet());
                    existing.setCity(tenantDto.getCity());
                    existing.setZipCode(tenantDto.getZipCode());
                    existing.setCountry(tenantDto.getCountry());
                    return toDto(tenantRepository.save(existing));
                })
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        TenantModel tenant = tenantRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .orElse(null);
        if (tenant == null) {
            return;
        }
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
