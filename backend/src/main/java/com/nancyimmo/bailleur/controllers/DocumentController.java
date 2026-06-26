package com.nancyimmo.bailleur.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.nancyimmo.bailleur.dto.DocumentDto;
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
