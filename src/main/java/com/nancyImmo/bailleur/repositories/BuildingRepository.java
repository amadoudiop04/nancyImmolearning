package com.nancyimmo.bailleur.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nancyimmo.bailleur.models.BuildingModel;

@Repository
public interface BuildingRepository extends JpaRepository<BuildingModel, Long> {
}
