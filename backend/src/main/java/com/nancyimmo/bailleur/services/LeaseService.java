package com.nancyimmo.bailleur.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.LeaseDto;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.models.LeaseModel;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;

    public LeaseService(LeaseRepository leaseRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository) {
        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
    }

    public LeaseDto create(LeaseDto dto) {
        LeaseModel model = toEntity(dto);
        if (model.getSignatureDate() == null) {
            model.setSignatureDate(LocalDate.now());
        }
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId()).ifPresent(model::setProperty);
        }
        if (dto.getTenantId() != null) {
            tenantRepository.findById(dto.getTenantId()).ifPresent(model::setTenant);
        }
        return toDto(leaseRepository.save(model));
    }

    public List<LeaseDto> findAll() {
        return leaseRepository.findAll().stream().map(this::toDto).toList();
    }

    public LeaseDto findById(Long id) {
        return leaseRepository.findById(id).map(this::toDto).orElse(null);
    }

    public LeaseDto update(Long id, LeaseDto dto) {
        return leaseRepository.findById(id)
                .map(existing -> {
                    existing.setStartDate(dto.getStartDate());
                    existing.setEndDate(dto.getEndDate());
                    existing.setRentAmount(dto.getRentAmount());
                    existing.setCurrency(dto.getCurrency());
                    if (dto.getSignatureDate() != null) {
                        existing.setSignatureDate(dto.getSignatureDate());
                    }
                    if (dto.getPropertyId() != null) {
                        propertyRepository.findById(dto.getPropertyId()).ifPresent(existing::setProperty);
                    }
                    if (dto.getTenantId() != null) {
                        tenantRepository.findById(dto.getTenantId()).ifPresent(existing::setTenant);
                    }
                    return toDto(leaseRepository.save(existing));
                })
                .orElse(null);
    }

    public void delete(Long id) {
        leaseRepository.deleteById(id);
    }

    private LeaseDto toDto(LeaseModel model) {
        LeaseDto dto = new LeaseDto(
                model.getId(),
                model.getSignatureDate(),
                model.getStartDate(),
                model.getEndDate(),
                model.getRentAmount(),
                model.getCurrency());
        dto.setPropertyId(model.getProperty() != null ? model.getProperty().getId() : null);
        dto.setTenantId(model.getTenant() != null ? model.getTenant().getId() : null);
        return dto;
    }

    private LeaseModel toEntity(LeaseDto dto) {
        LeaseModel model = new LeaseModel();
        model.setId(dto.getId());
        model.setSignatureDate(dto.getSignatureDate());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setRentAmount(dto.getRentAmount());
        model.setCurrency(dto.getCurrency());
        return model;
    }
}
