package com.nancyimmo.bailleur.repositories;

import org.springframework.stereotype.Repository;
import com.nancyimmo.bailleur.models.LeaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository

public interface LeaseRepository extends JpaRepository<LeaseModel, Long> {

}