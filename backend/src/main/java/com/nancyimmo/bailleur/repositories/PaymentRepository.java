package com.nancyimmo.bailleur.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nancyimmo.bailleur.models.PaymentModel;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {
    List<PaymentModel> findByLeaseId(Long leaseId);
    List<PaymentModel> findByStatus(String status);
}
