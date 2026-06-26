package com.nancyimmo.bailleur.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nancyimmo.bailleur.dto.DocumentDto;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.services.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public DocumentDto create(@RequestBody DocumentDto dto) {
        return documentService.create(dto);
    }

    @PostMapping("/generate-quittances")
    public List<DocumentDto> generateQuittances() {
        return documentService.generateQuittances();
    }

    /** Génère une quittance PDF pour un bail et un mois précis. */
    @PostMapping("/generate-quittance")
    public DocumentDto generateQuittance(@RequestBody Map<String, Object> body) {
        Long leaseId = Long.valueOf(String.valueOf(body.get("leaseId")));
        int year = Integer.parseInt(String.valueOf(body.get("year")));
        int month = Integer.parseInt(String.valueOf(body.get("month")));
        return documentService.generateQuittance(leaseId, year, month);
    }

    /** Génère un vrai contrat de bail PDF à partir d'un bail. */
    @PostMapping("/generate-bail")
    public DocumentDto generateBail(@RequestBody Map<String, Long> body) {
        return documentService.generateBail(body.get("leaseId"));
    }

    /** Upload d'une pièce justificative (multipart). */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentDto upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String docType,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long tenantId) {
        return documentService.upload(file, name, docType, propertyId, tenantId);
    }

    /** Téléchargement du fichier réel (PDF généré ou pièce uploadée). */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        DocumentModel doc = documentService.getForDownload(id);
        if (doc.getContent() == null || doc.getContent().length == 0) {
            return ResponseEntity.noContent().build();
        }
        String fileName = doc.getFileName() != null ? doc.getFileName() : ("document-" + id);
        String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(doc.getContent()));
    }

    @GetMapping
    public List<DocumentDto> getAll(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long tenantId) {
        if (propertyId != null) return documentService.findByPropertyId(propertyId);
        if (tenantId != null) return documentService.findByTenantId(tenantId);
        return documentService.findAll();
    }

    @GetMapping("/{id}")
    public DocumentDto getOne(@PathVariable Long id) {
        return documentService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        documentService.delete(id);
    }
}
