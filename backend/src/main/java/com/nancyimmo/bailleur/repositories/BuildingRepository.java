package com.nancyimmo.bailleur.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nancyimmo.bailleur.models.BuildingModel;
//un decorateur
@Repository
public interface BuildingRepository extends JpaRepository<BuildingModel, Long> {
    List<BuildingModel> findByLandlord_Email(String email);
    Optional<BuildingModel> findByIdAndLandlord_Email(Long id, String email);
}
