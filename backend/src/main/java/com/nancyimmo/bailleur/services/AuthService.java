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
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.security.JwtService;

@Service
public class AuthService {

    private static final String ROLE = "BAILLEUR";

    private final LandlordRepository landlordRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(LandlordRepository landlordRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.landlordRepository = landlordRepository;
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
}
