package com.nancyimmo.bailleur.repositories;

import org.springframework.stereotype.Repository;
import com.nancyimmo.bailleur.models.TenantModel;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TenantRepository extends JpaRepository<TenantModel, Long> {
}