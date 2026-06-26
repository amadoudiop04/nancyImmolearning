package com.nancyimmo.bailleur.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.repositories.LandlordRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LandlordRepository landlordRepository;

    public CustomUserDetailsService(LandlordRepository landlordRepository) {
        this.landlordRepository = landlordRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LandlordModel landlord = landlordRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Compte introuvable : " + email));

        if (landlord.getPassword() == null) {
            // Bailleur sans mot de passe (créé sans inscription) : pas de connexion possible.
            throw new UsernameNotFoundException("Aucun mot de passe défini pour : " + email);
        }

        return new User(
                landlord.getEmail(),
                landlord.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_BAILLEUR")));
    }
}
