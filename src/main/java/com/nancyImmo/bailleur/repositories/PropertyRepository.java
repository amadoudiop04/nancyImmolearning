package com.nancyImmo.bailleur.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nancyImmo.bailleur.models.PropertyModel;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyModel, Long> {
}
