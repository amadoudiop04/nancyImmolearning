package com.nancyimmo.bailleur.services;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.nancyimmo.bailleur.dto.PropertyDto;
import com.nancyimmo.bailleur.dto.PropertyDetailsDto;
import com.nancyimmo.bailleur.models.BuildingModel;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.models.TenantModel;
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

    public PropertyDetailsDto findDetailsById(Long id) {
        return propertyRepository.findWithDetailsById(id)
                .map(this::toDetailsDto)
                .orElse(null);
    }

    public List<PropertyDetailsDto> findAllDetails() {
        return propertyRepository.findAllWithDetailsBy()
                .stream()
                .map(this::toDetailsDto)
                .collect(Collectors.toList());
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
                model.getName(),
                model.getSize(),
                model.getKind(),
                model.getLocation());
    }

    private PropertyModel toEntity(PropertyDto dto) {
        PropertyModel model = new PropertyModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setSize(dto.getSize());
        model.setKind(dto.getKind());
        model.setLocation(dto.getLocation());
        return model;
    }

    private PropertyDetailsDto toDetailsDto(PropertyModel model) {
        PropertyDetailsDto dto = new PropertyDetailsDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setSize(model.getSize());
        dto.setKind(model.getKind());
        dto.setLocation(model.getLocation());

        dto.setBuilding(toBuildingInfo(model.getBuilding()));
        dto.setLandlord(toLandlordInfo(model.getLandlord()));

        LeaseModel leaseModel = model.getLease();
        dto.setLease(toLeaseInfo(leaseModel));
        dto.setTenant(leaseModel != null ? toTenantInfo(leaseModel.getTenant()) : null);

        return dto;
    }

    private PropertyDetailsDto.BuildingInfo toBuildingInfo(BuildingModel model) {
        if (model == null) {
            return null;
        }

        PropertyDetailsDto.BuildingInfo dto = new PropertyDetailsDto.BuildingInfo();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setStreet(model.getStreet());
        dto.setCity(model.getCity());
        dto.setZipCode(model.getZipCode());
        dto.setCountry(model.getCountry());
        return dto;
    }

    private PropertyDetailsDto.LandlordInfo toLandlordInfo(LandlordModel model) {
        if (model == null) {
            return null;
        }

        PropertyDetailsDto.LandlordInfo dto = new PropertyDetailsDto.LandlordInfo();
        dto.setId(model.getId());
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setEmail(model.getEmail());
        dto.setPhone(model.getPhone());
        dto.setStreet(model.getStreet());
        dto.setCity(model.getCity());
        dto.setZipCode(model.getZipCode());
        dto.setCountry(model.getCountry());
        return dto;
    }

    private PropertyDetailsDto.LeaseInfo toLeaseInfo(LeaseModel model) {
        if (model == null) {
            return null;
        }

        PropertyDetailsDto.LeaseInfo dto = new PropertyDetailsDto.LeaseInfo();
        dto.setId(model.getId());
        dto.setSignatureDate(model.getSignatureDate());
        dto.setStartDate(model.getStartDate());
        dto.setEndDate(model.getEndDate());
        dto.setRentAmount(model.getRentAmount());
        dto.setCurrency(model.getCurrency());
        return dto;
    }

    private PropertyDetailsDto.TenantInfo toTenantInfo(TenantModel model) {
        if (model == null) {
            return null;
        }

        PropertyDetailsDto.TenantInfo dto = new PropertyDetailsDto.TenantInfo();
        dto.setId(model.getId());
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setEmail(model.getEmail());
        dto.setPhone(model.getPhone());
        dto.setStreet(model.getStreet());
        dto.setCity(model.getCity());
        dto.setZipCode(model.getZipCode());
        dto.setCountry(model.getCountry());
        return dto;
    }
}