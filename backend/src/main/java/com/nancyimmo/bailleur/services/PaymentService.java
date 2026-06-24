package com.nancyimmo.bailleur.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.PaymentDto;
import com.nancyimmo.bailleur.dto.PaymentStatsDto;
import com.nancyimmo.bailleur.models.PaymentModel;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;

    public PaymentService(PaymentRepository paymentRepository, LeaseRepository leaseRepository) {
        this.paymentRepository = paymentRepository;
        this.leaseRepository = leaseRepository;
    }

    public PaymentDto create(PaymentDto dto) {
        PaymentModel model = toEntity(dto);
        if (dto.getLeaseId() != null) {
            leaseRepository.findById(dto.getLeaseId()).ifPresent(model::setLease);
        }
        return toDto(paymentRepository.save(model));
    }

    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PaymentDto> findByLeaseId(Long leaseId) {
        return paymentRepository.findByLeaseId(leaseId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PaymentDto> findByStatus(String status) {
        return paymentRepository.findByStatus(status).stream().map(this::toDto).collect(Collectors.toList());
    }

    public PaymentDto findById(Long id) {
        return paymentRepository.findById(id).map(this::toDto).orElse(null);
    }

    public PaymentDto update(Long id, PaymentDto dto) {
        return paymentRepository.findById(id)
                .map(existing -> {
                    existing.setPeriod(dto.getPeriod());
                    existing.setAmount(dto.getAmount());
                    existing.setStatus(dto.getStatus());
                    existing.setPaidDate(dto.getPaidDate());
                    return toDto(paymentRepository.save(existing));
                })
                .orElse(null);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    public PaymentStatsDto getStats() {
        List<PaymentModel> all = paymentRepository.findAll();
        BigDecimal paid = sum(all, "PAID");
        BigDecimal pending = sum(all, "PENDING");
        BigDecimal late = sum(all, "LATE");
        return new PaymentStatsDto(paid, pending, late);
    }

    private BigDecimal sum(List<PaymentModel> payments, String status) {
        return payments.stream()
                .filter(p -> status.equals(p.getStatus()) && p.getAmount() != null)
                .map(PaymentModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentDto toDto(PaymentModel model) {
        PaymentDto dto = new PaymentDto();
        dto.setId(model.getId());
        dto.setLeaseId(model.getLease() != null ? model.getLease().getId() : null);
        dto.setPeriod(model.getPeriod());
        dto.setAmount(model.getAmount());
        dto.setStatus(model.getStatus());
        dto.setPaidDate(model.getPaidDate());
        if (model.getLease() != null) {
            if (model.getLease().getTenant() != null) {
                dto.setTenantName(model.getLease().getTenant().getFirstName()
                        + " " + model.getLease().getTenant().getLastName());
            }
            if (model.getLease().getProperty() != null) {
                dto.setPropertyName(model.getLease().getProperty().getName());
            }
        }
        return dto;
    }

    private PaymentModel toEntity(PaymentDto dto) {
        PaymentModel model = new PaymentModel();
        model.setId(dto.getId());
        model.setPeriod(dto.getPeriod());
        model.setAmount(dto.getAmount());
        model.setStatus(dto.getStatus());
        model.setPaidDate(dto.getPaidDate());
        return model;
    }
}
