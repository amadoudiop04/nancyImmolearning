package com.nancyimmo.bailleur.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.DocumentDto;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;

    public DocumentService(DocumentRepository documentRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository) {
        this.documentRepository = documentRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
    }

    public DocumentDto create(DocumentDto dto) {
        DocumentModel model = toEntity(dto);
        if (model.getCreatedAt() == null) {
            model.setCreatedAt(LocalDate.now());
        }
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId()).ifPresent(model::setProperty);
        }
        if (dto.getTenantId() != null) {
            tenantRepository.findById(dto.getTenantId()).ifPresent(model::setTenant);
        }
        return toDto(documentRepository.save(model));
    }

    public List<DocumentDto> findAll() {
        return documentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<DocumentDto> findByPropertyId(Long propertyId) {
        return documentRepository.findByPropertyId(propertyId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<DocumentDto> findByTenantId(Long tenantId) {
        return documentRepository.findByTenantId(tenantId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public DocumentDto findById(Long id) {
        return documentRepository.findById(id).map(this::toDto).orElse(null);
    }

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    private DocumentDto toDto(DocumentModel model) {
        DocumentDto dto = new DocumentDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDocType(model.getDocType());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setPropertyId(model.getProperty() != null ? model.getProperty().getId() : null);
        dto.setTenantId(model.getTenant() != null ? model.getTenant().getId() : null);
        return dto;
    }

    private DocumentModel toEntity(DocumentDto dto) {
        DocumentModel model = new DocumentModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setDocType(dto.getDocType());
        model.setCreatedAt(dto.getCreatedAt());
        return model;
    }
}
