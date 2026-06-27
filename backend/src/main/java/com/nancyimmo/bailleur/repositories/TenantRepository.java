package com.nancyimmo.bailleur.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.nancyimmo.bailleur.models.TenantModel;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TenantRepository extends JpaRepository<TenantModel, Long> {
    List<TenantModel> findByLandlord_Email(String email);
    Optional<TenantModel> findByIdAndLandlord_Email(Long id, String email);

    // Authentification locataire
    Optional<TenantModel> findByEmail(String email);
    Optional<TenantModel> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
}
