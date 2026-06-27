package com.nancyimmo.bailleur.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nancyimmo.bailleur.dto.auth.AuthResponse;
import com.nancyimmo.bailleur.dto.auth.ForgotPasswordRequest;
import com.nancyimmo.bailleur.dto.auth.LoginRequest;
import com.nancyimmo.bailleur.dto.auth.RegisterRequest;
import com.nancyimmo.bailleur.dto.auth.ResetPasswordRequest;
import com.nancyimmo.bailleur.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou mot de passe incorrect."));
        }
    }

    /**
     * Demande de réinitialisation de mot de passe. En mode démo (pas d'envoi d'email), le jeton
     * est renvoyé directement dans la réponse ({@code resetToken}) pour permettre la suite du flux.
     * Le message reste générique afin de ne pas divulguer l'existence d'un compte.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = authService.forgotPassword(request.getEmail());
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Si un compte existe pour cet email, un lien de réinitialisation a été généré.");
        if (token != null) {
            body.put("resetToken", token);
        }
        return ResponseEntity.ok(body);
    }

    /** Réinitialise le mot de passe à partir d'un jeton valide et connecte directement l'utilisateur. */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            return ResponseEntity.ok(authService.resetPassword(request.getToken(), request.getNewPassword()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthResponse user = authService.currentUser(authentication.getName());
        return ResponseEntity.ok(user);
    }

    /** Suppression définitive du compte connecté et de toutes ses données. */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        authService.deleteAccount(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
