package com.nancyimmo.bailleur.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LandlordRepository landlordRepository;
    private final TenantRepository tenantRepository;

    public CustomUserDetailsService(LandlordRepository landlordRepository, TenantRepository tenantRepository) {
        this.landlordRepository = landlordRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1) On cherche d'abord un compte bailleur.
        LandlordModel landlord = landlordRepository.findByEmail(email).orElse(null);
        if (landlord != null && landlord.getPassword() != null) {
            return new User(landlord.getEmail(), landlord.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_BAILLEUR")));
        }

        // 2) Sinon, un compte locataire.
        TenantModel tenant = tenantRepository.findByEmail(email).orElse(null);
        if (tenant != null && tenant.getPassword() != null) {
            return new User(tenant.getEmail(), tenant.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_LOCATAIRE")));
        }

        throw new UsernameNotFoundException("Identifiants invalides pour : " + email);
    }
}
