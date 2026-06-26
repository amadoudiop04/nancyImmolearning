package com.nancyimmo.bailleur.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.DocumentDto;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;

    public DocumentService(DocumentRepository documentRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            LeaseRepository leaseRepository) {
        this.documentRepository = documentRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
    }

    /**
     * Génère automatiquement une quittance (document) pour le mois courant
     * pour chaque bail actif possédant un locataire. Idempotent : ne recrée
     * pas une quittance déjà présente pour le même bien et le même mois.
     */
    public List<DocumentDto> generateQuittances() {
        String monthLabel = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        List<DocumentDto> created = new ArrayList<>();

        for (LeaseModel lease : leaseRepository.findAll()) {
            if (lease.getProperty() == null || lease.getTenant() == null) {
                continue;
            }
            String name = "Quittance - " + lease.getProperty().getName() + " - " + monthLabel;
            boolean exists = documentRepository.findByPropertyId(lease.getProperty().getId()).stream()
                    .anyMatch(d -> name.equals(d.getName()));
            if (exists) {
                continue;
            }
            DocumentModel doc = new DocumentModel();
            doc.setName(name);
            doc.setDocType("QUITTANCE");
            doc.setCreatedAt(LocalDate.now());
            doc.setProperty(lease.getProperty());
            doc.setTenant(lease.getTenant());
            created.add(toDto(documentRepository.save(doc)));
        }
        return created;
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
