package com.nancyimmo.bailleur.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nancyimmo.bailleur.models.PropertyModel;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyModel, Long> {

	@EntityGraph(attributePaths = { "building", "landlord", "lease", "lease.tenant" })
	List<PropertyModel> findAllWithDetailsBy();

	@EntityGraph(attributePaths = { "building", "landlord", "lease", "lease.tenant" })
	Optional<PropertyModel> findWithDetailsById(Long id);
}
