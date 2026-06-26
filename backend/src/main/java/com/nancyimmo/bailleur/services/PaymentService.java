package com.nancyimmo.bailleur.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nancyimmo.bailleur.dto.PaymentDto;
import com.nancyimmo.bailleur.dto.PaymentStatsDto;
import com.nancyimmo.bailleur.dto.TenantPaymentHistoryDto;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PaymentModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PaymentRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.security.CurrentUser;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

@Service
public class PaymentService {

    private static final String[] MONTH_LABELS = {
            "Janv.", "Févr.", "Mars", "Avr.", "Mai", "Juin",
            "Juil.", "Août", "Sept.", "Oct.", "Nov.", "Déc."
    };

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;
    private final CurrentUser currentUser;
    private final StripeService stripeService;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public PaymentService(PaymentRepository paymentRepository,
            LeaseRepository leaseRepository,
            TenantRepository tenantRepository,
            CurrentUser currentUser,
            StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.leaseRepository = leaseRepository;
        this.tenantRepository = tenantRepository;
        this.currentUser = currentUser;
        this.stripeService = stripeService;
    }

    // ─── Paiement en ligne (Stripe Checkout) ─────────────────────────────────

    /** Crée une session Stripe Checkout et renvoie l'URL de paiement hébergée. */
    public String createCheckout(Long leaseId, String periodStr) {
        if (!stripeService.isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Paiement en ligne indisponible : Stripe non configuré (STRIPE_SECRET_KEY).");
        }
        String email = currentUser.requireEmail();
        LeaseModel lease = leaseRepository.findByIdAndProperty_Landlord_Email(leaseId, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bail introuvable."));
        if (lease.getRentAmount() == null || lease.getRentAmount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loyer non défini pour ce bail.");
        }
        YearMonth period = parsePeriod(periodStr);
        String successUrl = frontendUrl + "/locataire?paid=1&session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = frontendUrl + "/locataire?canceled=1";
        try {
            Session session = stripeService.createCheckoutSession(lease, period, successUrl, cancelUrl);
            return session.getUrl();
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erreur Stripe : " + e.getMessage());
        }
    }

    /** Confirme une session après retour de Stripe : enregistre le paiement si réglé. */
    public PaymentDto confirmCheckout(String sessionId) {
        if (sessionId == null || sessionId.isBlank() || !stripeService.isConfigured()) {
            return null;
        }
        String email = currentUser.requireEmail();
        try {
            Session session = stripeService.retrieveSession(sessionId);
            if (session == null || !"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                return null;
            }
            Long leaseId = Long.valueOf(session.getMetadata().get("leaseId"));
            YearMonth period = YearMonth.from(LocalDate.parse(session.getMetadata().get("period")));
            LeaseModel lease = leaseRepository.findByIdAndProperty_Landlord_Email(leaseId, email).orElse(null);
            if (lease == null) {
                return null;
            }
            return toDto(recordPaidPayment(lease, period));
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erreur Stripe : " + e.getMessage());
        }
    }

    /** Enregistre/màj le paiement d'un mois en PAID (idempotent par bail+mois). */
    @Transactional
    public PaymentModel recordPaidPayment(LeaseModel lease, YearMonth period) {
        PaymentModel existing = paymentRepository.findByLeaseId(lease.getId()).stream()
                .filter(p -> p.getPeriod() != null && YearMonth.from(p.getPeriod()).equals(period))
                .findFirst().orElse(null);
        if (existing != null) {
            existing.setStatus("PAID");
            if (existing.getPaidDate() == null) existing.setPaidDate(LocalDate.now());
            if (existing.getAmount() == null) existing.setAmount(lease.getRentAmount());
            return paymentRepository.save(existing);
        }
        PaymentModel pay = new PaymentModel();
        pay.setLease(lease);
        pay.setPeriod(period.atDay(1));
        pay.setAmount(lease.getRentAmount());
        pay.setStatus("PAID");
        pay.setPaidDate(LocalDate.now());
        return paymentRepository.save(pay);
    }

    /** Variante appelée par le webhook Stripe (système, sans utilisateur authentifié). */
    @Transactional
    public void recordPaidPaymentByLeaseId(Long leaseId, YearMonth period) {
        leaseRepository.findById(leaseId).ifPresent(lease -> recordPaidPayment(lease, period));
    }

    private YearMonth parsePeriod(String periodStr) {
        if (periodStr == null || periodStr.isBlank()) {
            return YearMonth.now();
        }
        try {
            // Accepte "yyyy-MM" ou une date "yyyy-MM-dd".
            return periodStr.length() <= 7 ? YearMonth.parse(periodStr) : YearMonth.from(LocalDate.parse(periodStr));
        } catch (Exception e) {
            return YearMonth.now();
        }
    }

    public PaymentDto create(PaymentDto dto) {
        PaymentModel model = toEntity(dto);
        // Le paiement doit porter sur un bail du bailleur connecté.
        if (dto.getLeaseId() != null) {
            leaseRepository.findByIdAndProperty_Landlord_Email(dto.getLeaseId(), currentUser.requireEmail())
                    .ifPresent(model::setLease);
        }
        return toDto(paymentRepository.save(model));
    }

    public List<PaymentDto> findAll() {
        return paymentRepository.findByLease_Property_Landlord_Email(currentUser.requireEmail())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PaymentDto> findByLeaseId(Long leaseId) {
        // Vérifie que le bail appartient bien au bailleur connecté.
        return leaseRepository.findByIdAndProperty_Landlord_Email(leaseId, currentUser.requireEmail())
                .map(lease -> paymentRepository.findByLeaseId(leaseId)
                        .stream().map(this::toDto).collect(Collectors.toList()))
                .orElseGet(List::of);
    }

    public List<PaymentDto> findByStatus(String status) {
        return paymentRepository.findByLease_Property_Landlord_EmailAndStatus(currentUser.requireEmail(), status)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public PaymentDto findById(Long id) {
        return paymentRepository.findById(id)
                .filter(this::ownedByCurrent)
                .map(this::toDto).orElse(null);
    }

    public PaymentDto update(Long id, PaymentDto dto) {
        return paymentRepository.findById(id)
                .filter(this::ownedByCurrent)
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
        paymentRepository.findById(id)
                .filter(this::ownedByCurrent)
                .ifPresent(p -> paymentRepository.deleteById(id));
    }

    public PaymentStatsDto getStats() {
        List<PaymentModel> all = paymentRepository.findByLease_Property_Landlord_Email(currentUser.requireEmail());
        BigDecimal paid = sum(all, "PAID");
        BigDecimal pending = sum(all, "PENDING");
        BigDecimal late = sum(all, "LATE");
        return new PaymentStatsDto(paid, pending, late);
    }

    /**
     * Historique de paiement d'un locataire sur une année : une case par mois
     * (encaissé / en attente / en retard) pour voir d'un coup d'œil les retards.
     */
    public TenantPaymentHistoryDto getTenantYearHistory(Long tenantId, int year) {
        String email = currentUser.requireEmail();
        TenantModel tenant = tenantRepository.findByIdAndLandlord_Email(tenantId, email).orElse(null);

        TenantPaymentHistoryDto dto = new TenantPaymentHistoryDto();
        dto.setTenantId(tenantId);
        dto.setYear(year);
        if (tenant == null) {
            dto.setMonths(new ArrayList<>());
            return dto;
        }
        dto.setTenantName(tenant.getFirstName() + " " + tenant.getLastName());

        List<LeaseModel> leases = leaseRepository.findByTenantId(tenantId);
        // Tous les paiements de ce locataire, indexés par mois de la période.
        List<PaymentModel> payments = new ArrayList<>();
        for (LeaseModel lease : leases) {
            payments.addAll(paymentRepository.findByLeaseId(lease.getId()));
            if (lease.getProperty() != null && dto.getPropertyName() == null) {
                dto.setPropertyName(lease.getProperty().getName());
            }
        }

        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalEncaisse = BigDecimal.ZERO;
        BigDecimal totalEnAttente = BigDecimal.ZERO;
        BigDecimal totalRetard = BigDecimal.ZERO;
        List<TenantPaymentHistoryDto.MonthCell> cells = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            final int month = m;
            YearMonth ym = YearMonth.of(year, month);

            // Loyer attendu = loyer du bail couvrant ce mois (le cas échéant).
            BigDecimal expectedRent = null;
            for (LeaseModel lease : leases) {
                if (coversMonth(lease, ym) && lease.getRentAmount() != null) {
                    expectedRent = lease.getRentAmount();
                    break;
                }
            }

            // Paiement enregistré pour ce mois ?
            PaymentModel payment = payments.stream()
                    .filter(p -> p.getPeriod() != null
                            && p.getPeriod().getYear() == year
                            && p.getPeriod().getMonthValue() == month)
                    .findFirst().orElse(null);

            String status;
            BigDecimal amount;
            LocalDate paidDate = null;

            if (payment != null) {
                status = payment.getStatus() != null ? payment.getStatus().toUpperCase() : "PENDING";
                amount = payment.getAmount() != null ? payment.getAmount()
                        : (expectedRent != null ? expectedRent : BigDecimal.ZERO);
                paidDate = payment.getPaidDate();
            } else if (expectedRent != null) {
                // Pas de paiement enregistré mais le loyer est dû.
                amount = expectedRent;
                status = ym.isAfter(currentMonth) ? "PENDING" : "LATE";
            } else {
                status = "NONE";
                amount = BigDecimal.ZERO;
            }

            switch (status) {
                case "PAID" -> totalEncaisse = totalEncaisse.add(amount);
                case "PENDING" -> totalEnAttente = totalEnAttente.add(amount);
                case "LATE" -> totalRetard = totalRetard.add(amount);
                default -> { /* hors bail : rien */ }
            }

            cells.add(new TenantPaymentHistoryDto.MonthCell(m, MONTH_LABELS[m - 1], status, amount, paidDate));
        }

        dto.setMonths(cells);
        dto.setTotalEncaisse(totalEncaisse);
        dto.setTotalEnAttente(totalEnAttente);
        dto.setTotalRetard(totalRetard);
        return dto;
    }

    private boolean coversMonth(LeaseModel lease, YearMonth ym) {
        if (lease.getStartDate() == null) {
            return false;
        }
        YearMonth start = YearMonth.from(lease.getStartDate());
        YearMonth end = lease.getEndDate() != null ? YearMonth.from(lease.getEndDate()) : YearMonth.now();
        return !ym.isBefore(start) && !ym.isAfter(end);
    }

    private boolean ownedByCurrent(PaymentModel p) {
        String email = currentUser.email();
        return email != null
                && p.getLease() != null
                && p.getLease().getProperty() != null
                && p.getLease().getProperty().getLandlord() != null
                && email.equalsIgnoreCase(p.getLease().getProperty().getLandlord().getEmail());
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
