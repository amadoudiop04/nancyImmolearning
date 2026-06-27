package com.nancyimmo.bailleur.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nancyimmo.bailleur.dto.DocumentDto;
import com.nancyimmo.bailleur.dto.DueMonthDto;
import com.nancyimmo.bailleur.dto.PaymentDto;
import com.nancyimmo.bailleur.dto.PropertyDetailsDto;
import com.nancyimmo.bailleur.dto.StatementLineDto;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.services.DocumentService;
import com.nancyimmo.bailleur.services.PaymentService;
import com.nancyimmo.bailleur.services.PortalService;

/**
 * Espace locataire : toutes les routes sont réservées au rôle LOCATAIRE (voir SecurityConfig) et
 * scopées sur le compte connecté. Un locataire accède ici à son bien, sa situation de compte,
 * ses documents et au paiement en ligne de son loyer.
 */
@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final PortalService portalService;
    private final DocumentService documentService;
    private final PaymentService paymentService;

    public PortalController(PortalService portalService,
            DocumentService documentService,
            PaymentService paymentService) {
        this.portalService = portalService;
        this.documentService = documentService;
        this.paymentService = paymentService;
    }

    /** Le bien loué par le locataire connecté (204 s'il n'a aucun bail). */
    @GetMapping("/property")
    public ResponseEntity<PropertyDetailsDto> myProperty() {
        PropertyDetailsDto dto = portalService.myProperty();
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();
    }

    /** Situation de compte (débit/crédit/solde) du locataire connecté. */
    @GetMapping("/statement")
    public List<StatementLineDto> myStatement() {
        return portalService.myStatement();
    }

    /** Mois impayés (arriérés en retard + mois courant) à régler par le locataire connecté. */
    @GetMapping("/dues")
    public List<DueMonthDto> myDues() {
        return portalService.myDues();
    }

    /** Documents associés au bien loué par le locataire connecté. */
    @GetMapping("/documents")
    public List<DocumentDto> myDocuments() {
        return documentService.findForCurrentTenant();
    }

    /** Téléchargement d'un document du locataire connecté. */
    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        DocumentModel doc = documentService.getForDownloadAsTenant(id);
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

    /** Paiement en ligne du loyer (Stripe Checkout) pour le bail du locataire connecté. */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> checkout(@RequestBody(required = false) Map<String, Object> body) {
        Long leaseId = portalLeaseId(body);
        String period = body != null && body.get("period") != null ? String.valueOf(body.get("period")) : null;
        String url = paymentService.createCheckoutAsTenant(leaseId, period);
        return ResponseEntity.ok(Map.of("url", url));
    }

    /** Confirme le paiement au retour de Stripe. */
    @PostMapping("/confirm")
    public PaymentDto confirm(@RequestBody Map<String, String> body) {
        return paymentService.confirmCheckoutAsTenant(body.get("sessionId"));
    }

    private Long portalLeaseId(Map<String, Object> body) {
        if (body != null && body.get("leaseId") != null) {
            return Long.valueOf(String.valueOf(body.get("leaseId")));
        }
        // Pas de leaseId fourni : on retombe sur le bail du bien du locataire connecté.
        PropertyDetailsDto property = portalService.myProperty();
        if (property == null || property.getLease() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Aucun bail à régler.");
        }
        return property.getLease().getId();
    }
}
