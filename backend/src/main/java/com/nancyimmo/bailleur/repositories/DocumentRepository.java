package com.nancyimmo.bailleur.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nancyimmo.bailleur.models.DocumentModel;

public interface DocumentRepository extends JpaRepository<DocumentModel, Long> {
    List<DocumentModel> findByPropertyId(Long propertyId);
    List<DocumentModel> findByTenantId(Long tenantId);

    // ─── Isolation par bailleur ───────────────────────────────────────────────
    List<DocumentModel> findByLandlord_EmailOrderByCreatedAtDesc(String email);
    List<DocumentModel> findByLandlord_EmailAndPropertyId(String email, Long propertyId);
    List<DocumentModel> findByLandlord_EmailAndTenantId(String email, Long tenantId);
    Optional<DocumentModel> findByIdAndLandlord_Email(Long id, String email);
}
