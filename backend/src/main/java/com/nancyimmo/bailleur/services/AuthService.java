package com.nancyimmo.bailleur.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.auth.AuthResponse;
import com.nancyimmo.bailleur.dto.auth.LoginRequest;
import com.nancyimmo.bailleur.dto.auth.RegisterRequest;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.models.UserModel;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.repositories.UserRepository;
import com.nancyimmo.bailleur.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LandlordRepository landlordRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
            LandlordRepository landlordRepository,
            TenantRepository tenantRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.landlordRepository = landlordRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest req) {
        if (req.getEmail() == null || req.getPassword() == null
                || req.getFirstName() == null || req.getLastName() == null) {
            throw new IllegalArgumentException("Champs obligatoires manquants.");
        }
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }

        String role = "LOCATAIRE".equalsIgnoreCase(req.getRole()) ? "LOCATAIRE" : "BAILLEUR";

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        userRepository.save(user);

        // Crée l'entité métier correspondante (Landlord ou Tenant) liée par l'email.
        if ("LOCATAIRE".equals(role)) {
            tenantRepository.findAll().stream()
                    .filter(t -> email.equalsIgnoreCase(t.getEmail()))
                    .findFirst()
                    .orElseGet(() -> {
                        TenantModel t = new TenantModel();
                        t.setFirstName(req.getFirstName());
                        t.setLastName(req.getLastName());
                        t.setEmail(email);
                        return tenantRepository.save(t);
                    });
        } else {
            landlordRepository.findAll().stream()
                    .filter(l -> email.equalsIgnoreCase(l.getEmail()))
                    .findFirst()
                    .orElseGet(() -> {
                        LandlordModel l = new LandlordModel();
                        l.setFirstName(req.getFirstName());
                        l.setLastName(req.getLastName());
                        l.setEmail(email);
                        return landlordRepository.save(l);
                    });
        }

        String token = jwtService.generateToken(email, role);
        return new AuthResponse(token, email, role, user.getFirstName(), user.getLastName());
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect.");
        }

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect."));

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getFirstName(), user.getLastName());
    }

    public AuthResponse currentUser(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable."));
        // Pas de nouveau token ici : on renvoie juste les infos du compte.
        return new AuthResponse(null, user.getEmail(), user.getRole(), user.getFirstName(), user.getLastName());
    }
}
