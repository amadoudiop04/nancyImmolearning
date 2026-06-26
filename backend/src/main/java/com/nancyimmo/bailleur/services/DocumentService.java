package com.nancyimmo.bailleur.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.nancyimmo.bailleur.dto.DocumentDto;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.security.CurrentUser;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final PdfService pdfService;
    private final CurrentUser currentUser;

    public DocumentService(DocumentRepository documentRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            PdfService pdfService,
            CurrentUser currentUser) {
        this.documentRepository = documentRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.pdfService = pdfService;
        this.currentUser = currentUser;
    }

    /**
     * Génère une vraie quittance PDF pour le mois courant, pour chaque bail
     * actif du bailleur connecté possédant un locataire. Idempotent par bien + mois.
     */
    public List<DocumentDto> generateQuittances() {
        LandlordModel landlord = currentUser.requireLandlord();
        String email = landlord.getEmail();
        YearMonth ym = YearMonth.now();
        String monthLabel = ym.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        List<DocumentDto> created = new ArrayList<>();

        for (LeaseModel lease : leaseRepository.findByProperty_Landlord_Email(email)) {
            if (lease.getProperty() == null || lease.getTenant() == null) {
                continue;
            }
            String name = "Quittance - " + lease.getProperty().getName() + " - " + monthLabel;
            boolean exists = documentRepository.findByPropertyId(lease.getProperty().getId()).stream()
                    .anyMatch(d -> name.equals(d.getName()));
            if (exists) {
                continue;
            }
            byte[] pdf = pdfService.buildQuittance(lease, ym, lease.getRentAmount());
            DocumentModel doc = new DocumentModel();
            doc.setName(name);
            doc.setDocType("QUITTANCE");
            doc.setCreatedAt(LocalDate.now());
            doc.setLandlord(landlord);
            doc.setProperty(lease.getProperty());
            doc.setTenant(lease.getTenant());
            doc.setContent(pdf);
            doc.setContentType("application/pdf");
            doc.setFileName(slug(name) + ".pdf");
            created.add(toDto(documentRepository.save(doc)));
        }
        return created;
    }

    /** Génère un vrai contrat de bail PDF pour un bail du bailleur connecté. */
    public DocumentDto generateBail(Long leaseId) {
        LandlordModel landlord = currentUser.requireLandlord();
        LeaseModel lease = leaseRepository.findByIdAndProperty_Landlord_Email(leaseId, landlord.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bail introuvable."));

        byte[] pdf = pdfService.buildBail(lease);
        String propertyName = lease.getProperty() != null ? lease.getProperty().getName() : "bien";
        String name = "Contrat de bail - " + propertyName;

        DocumentModel doc = new DocumentModel();
        doc.setName(name);
        doc.setDocType("LEASE");
        doc.setCreatedAt(LocalDate.now());
        doc.setLandlord(landlord);
        doc.setProperty(lease.getProperty());
        doc.setTenant(lease.getTenant());
        doc.setContent(pdf);
        doc.setContentType("application/pdf");
        doc.setFileName(slug(name) + ".pdf");
        return toDto(documentRepository.save(doc));
    }

    /** Génère une vraie quittance PDF pour un bail et un mois précis (idempotent par bien+mois). */
    public DocumentDto generateQuittance(Long leaseId, int year, int month) {
        LandlordModel landlord = currentUser.requireLandlord();
        LeaseModel lease = leaseRepository.findByIdAndProperty_Landlord_Email(leaseId, landlord.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bail introuvable."));
        if (lease.getProperty() == null || lease.getTenant() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le bail doit être associé à un bien et un locataire.");
        }
        YearMonth ym = YearMonth.of(year, month);
        String monthLabel = ym.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        String name = "Quittance - " + lease.getProperty().getName() + " - " + monthLabel;

        DocumentModel existing = documentRepository.findByPropertyId(lease.getProperty().getId()).stream()
                .filter(d -> name.equals(d.getName()))
                .findFirst().orElse(null);
        if (existing != null) {
            return toDto(existing);
        }

        byte[] pdf = pdfService.buildQuittance(lease, ym, lease.getRentAmount());
        DocumentModel doc = new DocumentModel();
        doc.setName(name);
        doc.setDocType("QUITTANCE");
        doc.setCreatedAt(LocalDate.now());
        doc.setLandlord(landlord);
        doc.setProperty(lease.getProperty());
        doc.setTenant(lease.getTenant());
        doc.setContent(pdf);
        doc.setContentType("application/pdf");
        doc.setFileName(slug(name) + ".pdf");
        return toDto(documentRepository.save(doc));
    }

    /** Upload d'une pièce justificative (ex. pièce d'identité fournie par le locataire). */
    public DocumentDto upload(MultipartFile file, String name, String docType, Long propertyId, Long tenantId) {
        LandlordModel landlord = currentUser.requireLandlord();
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier manquant.");
        }
        DocumentModel doc = new DocumentModel();
        doc.setName(name != null && !name.isBlank() ? name : file.getOriginalFilename());
        doc.setDocType(docType != null ? docType : "OTHER");
        doc.setCreatedAt(LocalDate.now());
        doc.setLandlord(landlord);
        if (propertyId != null) {
            propertyRepository.findByIdAndLandlord_Email(propertyId, landlord.getEmail()).ifPresent(doc::setProperty);
        }
        if (tenantId != null) {
            tenantRepository.findByIdAndLandlord_Email(tenantId, landlord.getEmail()).ifPresent(doc::setTenant);
        }
        try {
            doc.setContent(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lecture du fichier impossible.");
        }
        doc.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        doc.setFileName(file.getOriginalFilename());
        return toDto(documentRepository.save(doc));
    }

    /** Document complet (avec contenu binaire) pour téléchargement, limité au bailleur connecté. */
    public DocumentModel getForDownload(Long id) {
        return documentRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document introuvable."));
    }

    public DocumentDto create(DocumentDto dto) {
        DocumentModel model = toEntity(dto);
        model.setLandlord(currentUser.requireLandlord());
        if (model.getCreatedAt() == null) {
            model.setCreatedAt(LocalDate.now());
        }
        String email = currentUser.requireEmail();
        if (dto.getPropertyId() != null) {
            propertyRepository.findByIdAndLandlord_Email(dto.getPropertyId(), email).ifPresent(model::setProperty);
        }
        if (dto.getTenantId() != null) {
            tenantRepository.findByIdAndLandlord_Email(dto.getTenantId(), email).ifPresent(model::setTenant);
        }
        return toDto(documentRepository.save(model));
    }

    public List<DocumentDto> findAll() {
        return documentRepository.findByLandlord_EmailOrderByCreatedAtDesc(currentUser.requireEmail())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<DocumentDto> findByPropertyId(Long propertyId) {
        return documentRepository.findByLandlord_EmailAndPropertyId(currentUser.requireEmail(), propertyId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<DocumentDto> findByTenantId(Long tenantId) {
        return documentRepository.findByLandlord_EmailAndTenantId(currentUser.requireEmail(), tenantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public DocumentDto findById(Long id) {
        return documentRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .map(this::toDto).orElse(null);
    }

    public void delete(Long id) {
        documentRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .ifPresent(d -> documentRepository.deleteById(id));
    }

    private DocumentDto toDto(DocumentModel model) {
        DocumentDto dto = new DocumentDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDocType(model.getDocType());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setPropertyId(model.getProperty() != null ? model.getProperty().getId() : null);
        dto.setTenantId(model.getTenant() != null ? model.getTenant().getId() : null);
        dto.setHasFile(model.getContent() != null && model.getContent().length > 0);
        dto.setFileName(model.getFileName());
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

    private String slug(String s) {
        return s == null ? "document"
                : s.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
