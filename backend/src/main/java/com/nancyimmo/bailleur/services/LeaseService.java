package com.nancyimmo.bailleur.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.LeaseDto;
import com.nancyimmo.bailleur.dto.StatementLineDto;
import com.nancyimmo.bailleur.models.PaymentModel;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PaymentRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.models.LeaseModel;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;

    public LeaseService(LeaseRepository leaseRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            PaymentRepository paymentRepository) {
        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Construit la situation de compte d'un bail : un débit par mois échu
     * (loyer + charges) et un crédit par paiement reçu, avec solde courant.
     */
    public List<StatementLineDto> getStatement(Long leaseId) {
        LeaseModel lease = leaseRepository.findById(leaseId).orElse(null);
        if (lease == null) {
            return List.of();
        }

        List<StatementLineDto> lines = new ArrayList<>();
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        BigDecimal rent = lease.getRentAmount() != null ? lease.getRentAmount() : BigDecimal.ZERO;

        // Débits : un loyer dû par mois, du début du bail jusqu'au mois courant (borné par la fin du bail).
        if (lease.getStartDate() != null && rent.signum() > 0) {
            YearMonth start = YearMonth.from(lease.getStartDate());
            YearMonth now = YearMonth.now();
            YearMonth end = lease.getEndDate() != null ? YearMonth.from(lease.getEndDate()) : now;
            if (end.isAfter(now)) {
                end = now;
            }
            for (YearMonth ym = start; !ym.isAfter(end); ym = ym.plusMonths(1)) {
                LocalDate due = ym.atDay(Math.min(5, ym.lengthOfMonth()));
                String label = "Loyer + charges — " + ym.format(monthFmt);
                lines.add(new StatementLineDto(due, label, rent, null, null));
            }
        }

        // Crédits : les paiements encaissés (statut PAID).
        for (PaymentModel p : paymentRepository.findByLeaseId(leaseId)) {
            if (p.getAmount() == null) {
                continue;
            }
            boolean paid = "PAID".equalsIgnoreCase(p.getStatus());
            if (!paid) {
                continue;
            }
            LocalDate date = p.getPaidDate() != null ? p.getPaidDate()
                    : (p.getPeriod() != null ? p.getPeriod() : LocalDate.now());
            lines.add(new StatementLineDto(date, "Paiement reçu", null, p.getAmount(), null));
        }

        // Tri chronologique puis calcul du solde courant (débit augmente la dette, crédit la réduit).
        lines.sort(Comparator.comparing(StatementLineDto::getDate));
        BigDecimal balance = BigDecimal.ZERO;
        for (StatementLineDto line : lines) {
            if (line.getDebit() != null) {
                balance = balance.add(line.getDebit());
            }
            if (line.getCredit() != null) {
                balance = balance.subtract(line.getCredit());
            }
            line.setBalance(balance);
        }
        return lines;
    }

    public LeaseDto create(LeaseDto dto) {
        LeaseModel model = toEntity(dto);
        if (model.getSignatureDate() == null) {
            model.setSignatureDate(LocalDate.now());
        }
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId()).ifPresent(model::setProperty);
        }
        if (dto.getTenantId() != null) {
            tenantRepository.findById(dto.getTenantId()).ifPresent(model::setTenant);
        }
        return toDto(leaseRepository.save(model));
    }

    public List<LeaseDto> findAll() {
        return leaseRepository.findAll().stream().map(this::toDto).toList();
    }

    public LeaseDto findById(Long id) {
        return leaseRepository.findById(id).map(this::toDto).orElse(null);
    }

    public LeaseDto update(Long id, LeaseDto dto) {
        return leaseRepository.findById(id)
                .map(existing -> {
                    existing.setStartDate(dto.getStartDate());
                    existing.setEndDate(dto.getEndDate());
                    existing.setRentAmount(dto.getRentAmount());
                    existing.setCurrency(dto.getCurrency());
                    if (dto.getSignatureDate() != null) {
                        existing.setSignatureDate(dto.getSignatureDate());
                    }
                    if (dto.getPropertyId() != null) {
                        propertyRepository.findById(dto.getPropertyId()).ifPresent(existing::setProperty);
                    }
                    if (dto.getTenantId() != null) {
                        tenantRepository.findById(dto.getTenantId()).ifPresent(existing::setTenant);
                    }
                    return toDto(leaseRepository.save(existing));
                })
                .orElse(null);
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        com.nancyimmo.bailleur.models.LeaseModel lease = leaseRepository.findById(id).orElse(null);
        if (lease == null) {
            return;
        }
        // 1) Supprimer les paiements liés (clé étrangère lease_id).
        paymentRepository.deleteAll(paymentRepository.findByLeaseId(id));

        // 2) Rompre la relation bidirectionnelle OneToOne pour éviter le
        //    TransientPropertyValueException (PropertyModel.lease ↔ LeaseModel) au flush.
        com.nancyimmo.bailleur.models.PropertyModel property = lease.getProperty();
        if (property != null) {
            property.setLease(null);
        }
        lease.setProperty(null);
        lease.setTenant(null);

        // 3) Résilier le bail.
        leaseRepository.delete(lease);
    }

    private LeaseDto toDto(LeaseModel model) {
        LeaseDto dto = new LeaseDto(
                model.getId(),
                model.getSignatureDate(),
                model.getStartDate(),
                model.getEndDate(),
                model.getRentAmount(),
                model.getCurrency());
        dto.setPropertyId(model.getProperty() != null ? model.getProperty().getId() : null);
        dto.setTenantId(model.getTenant() != null ? model.getTenant().getId() : null);
        return dto;
    }

    private LeaseModel toEntity(LeaseDto dto) {
        LeaseModel model = new LeaseModel();
        model.setId(dto.getId());
        model.setSignatureDate(dto.getSignatureDate());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setRentAmount(dto.getRentAmount());
        model.setCurrency(dto.getCurrency());
        return model;
    }
}
