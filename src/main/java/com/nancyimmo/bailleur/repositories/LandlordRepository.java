package com.nancyimmo.bailleur.repositories;

import org.springframework.stereotype.Repository;
import com.nancyimmo.bailleur.models.LandlordModel;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LandlordRepository extends JpaRepository<LandlordModel, Long> {
}