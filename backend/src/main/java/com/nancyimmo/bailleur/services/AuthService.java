package com.nancyimmo.bailleur.services;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nancyimmo.bailleur.dto.auth.AuthResponse;
import com.nancyimmo.bailleur.dto.auth.LoginRequest;
import com.nancyimmo.bailleur.dto.auth.RegisterRequest;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.repositories.ApplicationRepository;
import com.nancyimmo.bailleur.repositories.BuildingRepository;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PaymentRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.security.JwtService;

@Service
public class AuthService {

    private static final String ROLE = "BAILLEUR";

    private final LandlordRepository landlordRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final BuildingRepository buildingRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;

    public AuthService(LandlordRepository landlordRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            BuildingRepository buildingRepository,
            LeaseRepository leaseRepository,
            PaymentRepository paymentRepository,
            DocumentRepository documentRepository,
            ApplicationRepository applicationRepository) {
        this.landlordRepository = landlordRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.buildingRepository = buildingRepository;
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
    }

    public AuthResponse register(RegisterRequest req) {
        if (req.getEmail() == null || req.getPassword() == null
                || req.getFirstName() == null || req.getLastName() == null) {
            throw new IllegalArgumentException("Champs obligatoires manquants.");
        }
        String email = req.getEmail().trim().toLowerCase();
        if (landlordRepository.existsByEmail(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }

        // Le bailleur EST le compte : on crée la fiche Landlord avec le mot de passe haché.
        LandlordModel landlord = new LandlordModel();
        landlord.setFirstName(req.getFirstName());
        landlord.setLastName(req.getLastName());
        landlord.setEmail(email);
        landlord.setPassword(passwordEncoder.encode(req.getPassword()));
        landlordRepository.save(landlord);

        String token = jwtService.generateToken(email, ROLE);
        return new AuthResponse(token, email, ROLE, landlord.getFirstName(), landlord.getLastName());
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect.");
        }

        LandlordModel landlord = landlordRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect."));

        String token = jwtService.generateToken(landlord.getEmail(), ROLE);
        return new AuthResponse(token, landlord.getEmail(), ROLE, landlord.getFirstName(), landlord.getLastName());
    }

    public AuthResponse currentUser(String email) {
        LandlordModel landlord = landlordRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Compte introuvable."));
        return new AuthResponse(null, landlord.getEmail(), ROLE, landlord.getFirstName(), landlord.getLastName());
    }

    /**
     * Supprime définitivement le compte du bailleur et TOUTES ses données associées
     * (biens, baux, paiements, documents, candidatures, locataires, immeubles).
     * L'ordre respecte les contraintes de clés étrangères.
     */
    @Transactional
    public void deleteAccount(String email) {
        LandlordModel landlord = landlordRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Compte introuvable."));

        // 1) Documents du bailleur (référencent biens/locataires).
        documentRepository.deleteAll(documentRepository.findByLandlord_EmailOrderByCreatedAtDesc(email));

        // 2) Pour chaque bien : candidatures, puis bail (+ ses paiements).
        List<PropertyModel> properties = propertyRepository.findByLandlord_Email(email);
        for (PropertyModel property : properties) {
            applicationRepository.deleteAll(applicationRepository.findByPropertyId(property.getId()));
            leaseRepository.findByPropertyId(property.getId()).ifPresent(lease -> {
                paymentRepository.deleteAll(paymentRepository.findByLeaseId(lease.getId()));
                lease.setProperty(null);
                lease.setTenant(null);
                property.setLease(null);
                leaseRepository.delete(lease);
            });
        }

        // 3) Biens.
        propertyRepository.deleteAll(properties);

        // 4) Locataires et immeubles du bailleur.
        tenantRepository.deleteAll(tenantRepository.findByLandlord_Email(email));
        buildingRepository.deleteAll(buildingRepository.findByLandlord_Email(email));

        // 5) Le compte lui-même.
        landlordRepository.delete(landlord);
    }
}
