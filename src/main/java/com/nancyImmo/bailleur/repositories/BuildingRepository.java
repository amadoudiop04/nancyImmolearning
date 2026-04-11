package com.nancyImmo.bailleur.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nancyImmo.bailleur.models.BuildingModel;

@Repository
public interface BuildingRepository extends JpaRepository< BuildingModel, Long> {
}
