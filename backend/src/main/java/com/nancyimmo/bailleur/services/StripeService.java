package com.nancyimmo.bailleur.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;

/** Intègre Stripe Checkout pour l'encaissement des loyers en ligne. */
@Service
public class StripeService {

    @Value("${stripe.secret-key:}")
    private String secretKey;

    @PostConstruct
    void init() {
        if (isConfigured()) {
            Stripe.apiKey = secretKey;
        }
    }

    public boolean isConfigured() {
        return secretKey != null && !secretKey.isBlank();
    }

    /** Crée une session Stripe Checkout pour le loyer d'un mois donné. */
    public Session createCheckoutSession(LeaseModel lease, YearMonth period,
            String successUrl, String cancelUrl) throws StripeException {
        PropertyModel property = lease.getProperty();
        String propertyName = property != null ? property.getName() : "Logement";
        String monthLabel = period.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));

        BigDecimal rent = lease.getRentAmount() != null ? lease.getRentAmount() : BigDecimal.ZERO;
        long amountCents = rent.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(amountCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Loyer " + propertyName + " — " + monthLabel)
                                        .build())
                                .build())
                        .build())
                .putMetadata("leaseId", String.valueOf(lease.getId()))
                .putMetadata("period", period.atDay(1).toString())
                .build();

        return Session.create(params);
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }
}
