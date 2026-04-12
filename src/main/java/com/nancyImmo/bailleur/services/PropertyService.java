package com.nancyimmo.bailleur.services;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.nancyimmo.bailleur.dto.PropertyDto;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.repositories.PropertyRepository;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyDto create(PropertyDto dto) {
        PropertyModel model = toEntity(dto);
        return toDto(propertyRepository.save(model));
    }

    public List<PropertyDto> findAll() {
        return propertyRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PropertyDto findById(Long id) {
        return propertyRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public PropertyDto update(Long id, PropertyDto dto) {
        PropertyModel model = toEntity(dto);
        model.setId(id);
        return toDto(propertyRepository.save(model));
    }

    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }

    private PropertyDto toDto(PropertyModel model) {
        return new PropertyDto(
                model.getId(),
                model.getDescription(),
                model.getTypeProperty(),
                model.getPrice());
    }

    private PropertyModel toEntity(PropertyDto dto) {
        PropertyModel model = new PropertyModel();
        model.setId(dto.getId());
        model.setDescription(dto.getDescription());
        model.setTypeProperty(dto.getTypeProperty());
        model.setPrice(dto.getPrice());
        return model;
    }
}