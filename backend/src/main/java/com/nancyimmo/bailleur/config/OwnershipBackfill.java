package com.nancyimmo.bailleur.config;

import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nancyimmo.bailleur.models.BuildingModel;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.repositories.BuildingRepository;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;

/**
 * Rattache au bailleur les lignes héritées (créées avant l'ajout de la colonne
 * landlord_id) : locataires (via leur bail → bien), immeubles (via un bien),
 * documents (via le bien ou le locataire). Idempotent — ne touche que les lignes
 * dont landlord_id est encore nul.
 */
@Component
@Order(2)
public class OwnershipBackfill implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final BuildingRepository buildingRepository;
    private final DocumentRepository documentRepository;
    private final PropertyRepository propertyRepository;
    private final LeaseRepository leaseRepository;

    public OwnershipBackfill(TenantRepository tenantRepository,
            BuildingRepository buildingRepository,
            DocumentRepository documentRepository,
            PropertyRepository propertyRepository,
            LeaseRepository leaseRepository) {
        this.tenantRepository = tenantRepository;
        this.buildingRepository = buildingRepository;
        this.documentRepository = documentRepository;
        this.propertyRepository = propertyRepository;
        this.leaseRepository = leaseRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Locataires : propriétaire = bailleur du bien loué.
        for (TenantModel tenant : tenantRepository.findAll()) {
            if (tenant.getLandlord() != null) {
                continue;
            }
            leaseRepository.findByTenantId(tenant.getId()).stream()
                    .map(l -> l.getProperty())
                    .filter(Objects::nonNull)
                    .map(PropertyModel::getLandlord)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresent(landlord -> {
                        tenant.setLandlord(landlord);
                        tenantRepository.save(tenant);
                    });
        }

        // Immeubles : propriétaire = bailleur d'un de ses biens.
        for (BuildingModel building : buildingRepository.findAll()) {
            if (building.getLandlord() != null) {
                continue;
            }
            propertyRepository.findAll().stream()
                    .filter(p -> p.getBuilding() != null
                            && Objects.equals(p.getBuilding().getId(), building.getId()))
                    .map(PropertyModel::getLandlord)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresent(landlord -> {
                        building.setLandlord(landlord);
                        buildingRepository.save(building);
                    });
        }

        // Documents : propriétaire = bailleur du bien lié, sinon du locataire lié.
        for (DocumentModel doc : documentRepository.findAll()) {
            if (doc.getLandlord() != null) {
                continue;
            }
            LandlordModel landlord = null;
            if (doc.getProperty() != null) {
                landlord = doc.getProperty().getLandlord();
            }
            if (landlord == null && doc.getTenant() != null) {
                landlord = doc.getTenant().getLandlord();
            }
            if (landlord != null) {
                doc.setLandlord(landlord);
                documentRepository.save(doc);
            }
        }
    }
}
