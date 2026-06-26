package com.nancyimmo.bailleur.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.BuildingDto;
import com.nancyimmo.bailleur.models.BuildingModel;
import com.nancyimmo.bailleur.repositories.BuildingRepository;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public BuildingDto create(BuildingDto dto) {
        BuildingModel model = toEntity(dto);
        return toDto(buildingRepository.save(model));
    }

    public List<BuildingDto> findAll() {
        return buildingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }       

    public BuildingDto findById(Long id) {
        return buildingRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public BuildingDto update(Long id, BuildingDto dto) {
        BuildingModel model = toEntity(dto);
        model.setId(id);
        return toDto(buildingRepository.save(model));
    }

    public void delete(Long id) {
        buildingRepository.deleteById(id);
    }

    private BuildingDto toDto(BuildingModel model) {
        return new BuildingDto(
                model.getId(),
                model.getName(),
                model.getStreet(),
                model.getCity(),
                model.getZipCode(),
                model.getCountry());
    }

    private BuildingModel toEntity(BuildingDto dto) {
        BuildingModel model = new BuildingModel();
        if (dto.getId() != null) {
            model.setId(dto.getId());
        }
        model.setName(dto.getName());
        model.setStreet(dto.getStreet());
        model.setCity(dto.getCity());
        model.setZipCode(dto.getZipCode());
        model.setCountry(dto.getCountry());
        return model;
    }
}
