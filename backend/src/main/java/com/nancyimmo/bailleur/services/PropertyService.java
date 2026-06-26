package com.nancyimmo.bailleur.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import com.nancyimmo.bailleur.dto.PropertyDto;
import com.nancyimmo.bailleur.dto.PropertyDetailsDto;
import com.nancyimmo.bailleur.models.BuildingModel;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.repositories.BuildingRepository;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PaymentRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final BuildingRepository buildingRepository;
    private final LandlordRepository landlordRepository;
    private final DocumentRepository documentRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;

    public PropertyService(PropertyRepository propertyRepository,
            BuildingRepository buildingRepository,
            LandlordRepository landlordRepository,
            DocumentRepository documentRepository,
            LeaseRepository leaseRepository,
            PaymentRepository paymentRepository) {
        this.propertyRepository = propertyRepository;
        this.buildingRepository = buildingRepository;
        this.landlordRepository = landlordRepository;
        this.documentRepository = documentRepository;
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
    }

    public PropertyDto create(PropertyDto dto) {
        PropertyModel model = toEntity(dto);
        if (dto.getBuildingId() != null) {
            buildingRepository.findById(dto.getBuildingId()).ifPresent(model::setBuilding);
        }
        if (dto.getLandlordId() != null) {
            landlordRepository.findById(dto.getLandlordId()).ifPresent(model::setLandlord);
        }
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

    public List<PropertyDetailsDto> findAvailable() {
        return propertyRepository.findAllWithDetailsBy().stream()
                .filter(p -> p.getLease() == null)
                .map(this::toDetailsDto)
                .collect(Collectors.toList());
    }

    public PropertyDto update(Long id, PropertyDto dto) {
        return propertyRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setSize(dto.getSize());
                    existing.setKind(dto.getKind());
                    existing.setLocation(dto.getLocation());
                    existing.setRent(dto.getRent());
                    if (dto.getDescription() != null) {
                        existing.setDescription(dto.getDescription());
                    }
                    if (dto.getImageUrl() != null) {
                        existing.setImageUrl(dto.getImageUrl());
                    }
                    if (dto.getBuildingId() != null) {
                        buildingRepository.findById(dto.getBuildingId()).ifPresent(existing::setBuilding);
                    }
                    if (dto.getLandlordId() != null) {
                        landlordRepository.findById(dto.getLandlordId()).ifPresent(existing::setLandlord);
                    }
                    return toDto(propertyRepository.save(existing));
                })
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        // Supprime d'abord les enregistrements dépendants pour éviter les violations de clé étrangère.
        documentRepository.deleteAll(documentRepository.findByPropertyId(id));

        leaseRepository.findByPropertyId(id).ifPresent(lease -> {
            paymentRepository.deleteAll(paymentRepository.findByLeaseId(lease.getId()));
            leaseRepository.delete(lease);
        });

        propertyRepository.deleteById(id);
    }

    private PropertyDto toDto(PropertyModel model) {
        PropertyDto dto = new PropertyDto(
                model.getId(),
                model.getName(),
                model.getSize(),
                model.getKind(),
                model.getLocation());
        dto.setDescription(model.getDescription());
        dto.setImageUrl(model.getImageUrl());
        dto.setRent(model.getRent());
        dto.setBuildingId(model.getBuilding() != null ? model.getBuilding().getId() : null);
        dto.setLandlordId(model.getLandlord() != null ? model.getLandlord().getId() : null);
        return dto;
    }

    private PropertyModel toEntity(PropertyDto dto) {
        PropertyModel model = new PropertyModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setSize(dto.getSize());
        model.setKind(dto.getKind());
        model.setLocation(dto.getLocation());
        // La colonne "description" existante est NOT NULL : on garantit une valeur non nulle.
        model.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        model.setImageUrl(dto.getImageUrl());
        model.setRent(dto.getRent());
        return model;
    }

    private PropertyDetailsDto toDetailsDto(PropertyModel model) {
        PropertyDetailsDto dto = new PropertyDetailsDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setSize(model.getSize());
        dto.setKind(model.getKind());
        dto.setLocation(model.getLocation());
        dto.setDescription(model.getDescription());
        dto.setImageUrl(model.getImageUrl());
        dto.setRent(model.getRent());

        dto.setBuilding(toBuildingInfo(model.getBuilding()));
        dto.setLandlord(toLandlordInfo(model.getLandlord()));

        LeaseModel leaseModel = model.getLease();
        dto.setLease(toLeaseInfo(leaseModel));
        dto.setTenant(leaseModel != null ? toTenantInfo(leaseModel.getTenant()) : null);

        return dto;
    }

    private PropertyDetailsDto.BuildingInfo toBuildingInfo(BuildingModel model) {
        if (model == null) return null;
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
        if (model == null) return null;
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
        if (model == null) return null;
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
        if (model == null) return null;
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
