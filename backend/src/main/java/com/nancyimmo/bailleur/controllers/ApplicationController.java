package com.nancyimmo.bailleur.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.nancyimmo.bailleur.dto.ApplicationDto;
import com.nancyimmo.bailleur.services.ApplicationService;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Public : un candidat dépose son dossier (depuis la recherche).
    @PostMapping
    public ApplicationDto create(@RequestBody ApplicationDto dto) {
        return applicationService.create(dto);
    }

    // Protégé : le bailleur ne voit QUE les candidatures déposées sur ses propres biens.
    @GetMapping
    public List<ApplicationDto> getAll(Authentication authentication,
            @RequestParam(required = false) Long propertyId) {
        String email = authentication != null ? authentication.getName() : null;
        if (propertyId != null) {
            return applicationService.findByProperty(propertyId, email);
        }
        return applicationService.findForLandlord(email);
    }

    @PutMapping("/{id}/status")
    public ApplicationDto updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        if (!applicationService.ownedByLandlord(id, email)) {
            return null;
        }
        return applicationService.updateStatus(id, body.getOrDefault("status", "PENDING"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        if (applicationService.ownedByLandlord(id, email)) {
            applicationService.delete(id);
        }
    }
}
