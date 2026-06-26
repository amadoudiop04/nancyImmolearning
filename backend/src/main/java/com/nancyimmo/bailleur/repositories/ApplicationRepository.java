package com.nancyimmo.bailleur.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nancyimmo.bailleur.models.ApplicationModel;

public interface ApplicationRepository extends JpaRepository<ApplicationModel, Long> {
    List<ApplicationModel> findByPropertyId(Long propertyId);
    List<ApplicationModel> findByStatus(String status);
    List<ApplicationModel> findAllByOrderByCreatedAtDesc();

    // ─── Isolation par bailleur (via le bien) ─────────────────────────────────
    List<ApplicationModel> findByProperty_Landlord_EmailOrderByCreatedAtDesc(String email);
}
