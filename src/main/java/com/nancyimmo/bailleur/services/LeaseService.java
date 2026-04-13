package com.nancyimmo.bailleur.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.LeaseDto;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.models.LeaseModel;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;

    public LeaseService(LeaseRepository leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

    public LeaseDto create(LeaseDto dto) {
        LeaseModel model = toEntity(dto);
        if (model.getSignatureDate() == null) {
            model.setSignatureDate(LocalDate.now());
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
                    return toDto(leaseRepository.save(existing));
                })
                .orElse(null);
    }

    public void delete(Long id) {
        leaseRepository.deleteById(id);
    }

    private LeaseDto toDto(LeaseModel model) {
        return new LeaseDto(
                model.getId(),
                model.getSignatureDate(),
                model.getStartDate(),
                model.getEndDate(),
                model.getRentAmount(),
                model.getCurrency());
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
