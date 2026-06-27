package com.nancyimmo.bailleur.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;

/**
 * Résout le bailleur actuellement authentifié à partir du contexte de sécurité (JWT).
 * Centralise l'isolation des données : chaque bailleur ne manipule que ses propres
 * biens, locataires, baux, paiements et documents.
 */
@Component
public class CurrentUser {

    private final LandlordRepository landlordRepository;
    private final TenantRepository tenantRepository;

    public CurrentUser(LandlordRepository landlordRepository, TenantRepository tenantRepository) {
        this.landlordRepository = landlordRepository;
        this.tenantRepository = tenantRepository;
    }

    /** Email du compte connecté, ou null si la requête est anonyme. */
    public String email() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String name = auth.getName();
        return (name == null || "anonymousUser".equals(name)) ? null : name;
    }

    /** Bailleur connecté, ou null si anonyme / introuvable. */
    public LandlordModel landlord() {
        String email = email();
        if (email == null) {
            return null;
        }
        return landlordRepository.findByEmail(email).orElse(null);
    }

    /** Bailleur connecté ; lève 401 si non authentifié. */
    public LandlordModel requireLandlord() {
        LandlordModel landlord = landlord();
        if (landlord == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise.");
        }
        return landlord;
    }

    /** Email du bailleur connecté ; lève 401 si non authentifié. */
    public String requireEmail() {
        String email = email();
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise.");
        }
        return email;
    }

    /** Locataire connecté, ou null si anonyme / introuvable. */
    public TenantModel tenant() {
        String email = email();
        if (email == null) {
            return null;
        }
        return tenantRepository.findByEmail(email).orElse(null);
    }

    /** Locataire connecté ; lève 401 si non authentifié / introuvable. */
    public TenantModel requireTenant() {
        TenantModel tenant = tenant();
        if (tenant == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification locataire requise.");
        }
        return tenant;
    }
}
