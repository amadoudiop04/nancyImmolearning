package com.nancyimmo.bailleur.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.nancyimmo.bailleur.models.LeaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LeaseRepository extends JpaRepository<LeaseModel, Long> {
    Optional<LeaseModel> findByPropertyId(Long propertyId);
    List<LeaseModel> findByTenantId(Long tenantId);

    // ─── Isolation par bailleur (via le bien) ─────────────────────────────────
    List<LeaseModel> findByProperty_Landlord_Email(String email);
    Optional<LeaseModel> findByIdAndProperty_Landlord_Email(Long id, String email);
}
