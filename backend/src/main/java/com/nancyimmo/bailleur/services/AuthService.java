package com.nancyimmo.bailleur.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.nancyimmo.bailleur.models.TenantModel;
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
    private static final String ROLE_TENANT = "LOCATAIRE";
    private static final long RESET_TOKEN_TTL_MINUTES = 30;

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
        boolean wantsTenant = ROLE_TENANT.equalsIgnoreCase(req.getRole());

        // Un email ne peut pas désigner à la fois un bailleur et un locataire.
        if (landlordRepository.existsByEmail(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }

        if (wantsTenant) {
            return registerTenant(req, email);
        }

        // Bailleur (par défaut) : le bailleur EST le compte.
        TenantModel collidingTenant = tenantRepository.findByEmail(email).orElse(null);
        if (collidingTenant != null && collidingTenant.getPassword() != null) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }
        LandlordModel landlord = new LandlordModel();
        landlord.setFirstName(req.getFirstName());
        landlord.setLastName(req.getLastName());
        landlord.setEmail(email);
        landlord.setPassword(passwordEncoder.encode(req.getPassword()));
        landlordRepository.save(landlord);

        String token = jwtService.generateToken(email, ROLE);
        return new AuthResponse(token, email, ROLE, landlord.getFirstName(), landlord.getLastName());
    }

    /**
     * Inscription locataire. Si une fiche locataire existe déjà pour cet email (créée par un
     * bailleur), on l'« active » en lui attribuant un mot de passe — le locataire récupère ainsi
     * l'accès à son bail. Sinon on crée une fiche locataire autonome (sans bien tant qu'un
     * bailleur ne l'a pas rattachée).
     */
    private AuthResponse registerTenant(RegisterRequest req, String email) {
        TenantModel tenant = tenantRepository.findByEmail(email).orElse(null);
        if (tenant != null && tenant.getPassword() != null) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }
        if (tenant == null) {
            tenant = new TenantModel();
            tenant.setEmail(email);
            tenant.setFirstName(req.getFirstName());
            tenant.setLastName(req.getLastName());
        } else {
            if (tenant.getFirstName() == null || tenant.getFirstName().isBlank()) {
                tenant.setFirstName(req.getFirstName());
            }
            if (tenant.getLastName() == null || tenant.getLastName().isBlank()) {
                tenant.setLastName(req.getLastName());
            }
        }
        tenant.setPassword(passwordEncoder.encode(req.getPassword()));
        tenantRepository.save(tenant);

        String token = jwtService.generateToken(email, ROLE_TENANT);
        return new AuthResponse(token, email, ROLE_TENANT, tenant.getFirstName(), tenant.getLastName());
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect.");
        }
        return buildAuthResponse(email, true);
    }

    /** Construit la réponse d'auth en détectant le rôle (bailleur ou locataire) à partir de l'email. */
    private AuthResponse buildAuthResponse(String email, boolean withToken) {
        LandlordModel landlord = landlordRepository.findByEmail(email).orElse(null);
        if (landlord != null && landlord.getPassword() != null) {
            String token = withToken ? jwtService.generateToken(email, ROLE) : null;
            return new AuthResponse(token, landlord.getEmail(), ROLE, landlord.getFirstName(), landlord.getLastName());
        }
        TenantModel tenant = tenantRepository.findByEmail(email).orElse(null);
        if (tenant != null && tenant.getPassword() != null) {
            String token = withToken ? jwtService.generateToken(email, ROLE_TENANT) : null;
            return new AuthResponse(token, tenant.getEmail(), ROLE_TENANT, tenant.getFirstName(), tenant.getLastName());
        }
        throw new BadCredentialsException("Email ou mot de passe incorrect.");
    }

    /**
     * Demande de réinitialisation : génère un jeton à usage unique (valable 30 min) pour le
     * compte correspondant à l'email. Retourne le jeton si le compte existe, sinon {@code null}
     * (le contrôleur renvoie alors un message générique sans divulguer l'existence du compte).
     */
    @Transactional
    public String forgotPassword(String rawEmail) {
        String email = rawEmail == null ? "" : rawEmail.trim().toLowerCase();

        LandlordModel landlord = landlordRepository.findByEmail(email).orElse(null);
        if (landlord != null && landlord.getPassword() != null) {
            String token = newResetToken();
            landlord.setResetToken(token);
            landlord.setResetTokenExpiry(Instant.now().plus(RESET_TOKEN_TTL_MINUTES, ChronoUnit.MINUTES));
            landlordRepository.save(landlord);
            return token;
        }

        TenantModel tenant = tenantRepository.findByEmail(email).orElse(null);
        if (tenant != null && tenant.getPassword() != null) {
            String token = newResetToken();
            tenant.setResetToken(token);
            tenant.setResetTokenExpiry(Instant.now().plus(RESET_TOKEN_TTL_MINUTES, ChronoUnit.MINUTES));
            tenantRepository.save(tenant);
            return token;
        }

        return null;
    }

    private String newResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Réinitialise le mot de passe à partir d'un jeton valide (bailleur OU locataire), puis
     * connecte directement le compte (renvoie un JWT). Le jeton est invalidé après usage.
     */
    @Transactional
    public AuthResponse resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank() || newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Lien invalide ou mot de passe trop court (6 caractères minimum).");
        }

        LandlordModel landlord = landlordRepository.findByResetToken(token).orElse(null);
        if (landlord != null) {
            requireValidExpiry(landlord.getResetTokenExpiry());
            landlord.setPassword(passwordEncoder.encode(newPassword));
            landlord.setResetToken(null);
            landlord.setResetTokenExpiry(null);
            landlordRepository.save(landlord);
            String jwt = jwtService.generateToken(landlord.getEmail(), ROLE);
            return new AuthResponse(jwt, landlord.getEmail(), ROLE, landlord.getFirstName(), landlord.getLastName());
        }

        TenantModel tenant = tenantRepository.findByResetToken(token).orElse(null);
        if (tenant != null) {
            requireValidExpiry(tenant.getResetTokenExpiry());
            tenant.setPassword(passwordEncoder.encode(newPassword));
            tenant.setResetToken(null);
            tenant.setResetTokenExpiry(null);
            tenantRepository.save(tenant);
            String jwt = jwtService.generateToken(tenant.getEmail(), ROLE_TENANT);
            return new AuthResponse(jwt, tenant.getEmail(), ROLE_TENANT, tenant.getFirstName(), tenant.getLastName());
        }

        throw new IllegalArgumentException("Lien de réinitialisation invalide.");
    }

    private void requireValidExpiry(Instant expiry) {
        if (expiry == null || expiry.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Ce lien a expiré. Veuillez refaire une demande.");
        }
    }

    public AuthResponse currentUser(String email) {
        return buildAuthResponse(email, false);
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
