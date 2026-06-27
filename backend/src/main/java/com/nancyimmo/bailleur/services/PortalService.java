package com.nancyimmo.bailleur.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nancyimmo.bailleur.dto.PropertyDetailsDto;
import com.nancyimmo.bailleur.dto.StatementLineDto;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.security.CurrentUser;

/**
 * Portail locataire : expose uniquement les données du bien loué par le locataire connecté
 * (description du bien, situation de compte, etc.). Garantit l'isolation : un locataire ne voit
 * jamais les données d'un autre locataire ni l'espace de gestion du bailleur.
 */
@Service
public class PortalService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final LeaseService leaseService;
    private final CurrentUser currentUser;

    public PortalService(LeaseRepository leaseRepository,
            PropertyRepository propertyRepository,
            PropertyService propertyService,
            LeaseService leaseService,
            CurrentUser currentUser) {
        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
        this.leaseService = leaseService;
        this.currentUser = currentUser;
    }

    /** Le bien actuellement loué par le locataire connecté (avec bail, immeuble, bailleur), ou null. */
    @Transactional(readOnly = true)
    public PropertyDetailsDto myProperty() {
        LeaseModel lease = currentLeaseWithProperty();
        if (lease == null) {
            return null;
        }
        return propertyRepository.findWithDetailsById(lease.getProperty().getId())
                .map(propertyService::toDetailsDto)
                .orElse(null);
    }

    /** Situation de compte (débits/crédits) du bail du locataire connecté. */
    @Transactional(readOnly = true)
    public List<StatementLineDto> myStatement() {
        return leaseService.buildStatement(currentLeaseWithProperty());
    }

    /** Bail du locataire connecté portant sur un bien (le premier trouvé). */
    private LeaseModel currentLeaseWithProperty() {
        String email = currentUser.requireTenant().getEmail();
        return leaseRepository.findByTenant_Email(email).stream()
                .filter(l -> l.getProperty() != null)
                .findFirst()
                .orElse(null);
    }
}
