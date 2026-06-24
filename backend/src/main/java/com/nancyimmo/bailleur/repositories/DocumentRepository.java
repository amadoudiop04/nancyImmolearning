package com.nancyimmo.bailleur.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nancyimmo.bailleur.models.DocumentModel;

public interface DocumentRepository extends JpaRepository<DocumentModel, Long> {
    List<DocumentModel> findByPropertyId(Long propertyId);
    List<DocumentModel> findByTenantId(Long tenantId);
}
