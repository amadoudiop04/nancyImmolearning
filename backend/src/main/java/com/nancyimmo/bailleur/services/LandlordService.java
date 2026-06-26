package com.nancyimmo.bailleur.services;

import org.springframework.stereotype.Service;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.dto.LandlordDto;
import com.nancyimmo.bailleur.models.LandlordModel;

@Service
public class LandlordService {

    private final LandlordRepository landlordRepository;

    public LandlordService(LandlordRepository landlordRepository) {
        this.landlordRepository = landlordRepository;
    }

    public LandlordDto createLandlord(LandlordDto landlordDto) {
        LandlordModel model = toEntity(landlordDto);
        return toDto(landlordRepository.save(model));
    }

    public java.util.List<LandlordDto> findAll() {
        return landlordRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public void delete(Long id) {
        landlordRepository.deleteById(id);
    }

    public LandlordDto findById(Long id) {
        return landlordRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public LandlordDto update(Long id, LandlordDto landlordDto) {
        // On charge l'existant pour NE PAS écraser le mot de passe (absent du DTO).
        return landlordRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(landlordDto.getFirstName());
                    existing.setLastName(landlordDto.getLastName());
                    existing.setEmail(landlordDto.getEmail());
                    existing.setPhone(landlordDto.getPhone());
                    existing.setStreet(landlordDto.getStreet());
                    existing.setCity(landlordDto.getCity());
                    existing.setZipCode(landlordDto.getZipCode());
                    existing.setCountry(landlordDto.getCountry());
                    return toDto(landlordRepository.save(existing));
                })
                .orElse(null);
    }

    private LandlordDto toDto(LandlordModel model) {
        return new LandlordDto(
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

    private LandlordModel toEntity(LandlordDto dto) {
        LandlordModel model = new LandlordModel();
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