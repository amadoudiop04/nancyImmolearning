package com.nancyimmo.bailleur.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nancyimmo.bailleur.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
}
