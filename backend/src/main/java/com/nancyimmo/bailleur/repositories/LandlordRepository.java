package com.nancyimmo.bailleur.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.nancyimmo.bailleur.models.LandlordModel;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LandlordRepository extends JpaRepository<LandlordModel, Long> {
    Optional<LandlordModel> findByEmail(String email);
    Optional<LandlordModel> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
}
