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

	// ─── Isolation par bailleur ───────────────────────────────────────────────
	@EntityGraph(attributePaths = { "building", "landlord", "lease", "lease.tenant" })
	List<PropertyModel> findAllWithDetailsByLandlord_Email(String email);

	@EntityGraph(attributePaths = { "building", "landlord", "lease", "lease.tenant" })
	Optional<PropertyModel> findWithDetailsByIdAndLandlord_Email(Long id, String email);

	List<PropertyModel> findByLandlord_Email(String email);

	Optional<PropertyModel> findByIdAndLandlord_Email(Long id, String email);

	long countByLandlord_Email(String email);
}
