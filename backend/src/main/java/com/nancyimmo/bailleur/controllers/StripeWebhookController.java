package com.nancyimmo.bailleur.controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nancyimmo.bailleur.services.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

/**
 * Réception des événements Stripe (optionnel, recommandé en production).
 * Sur "checkout.session.completed" payé, enregistre le paiement côté serveur,
 * indépendamment du retour navigateur. Actif uniquement si STRIPE_WEBHOOK_SECRET
 * est renseigné. Endpoint PUBLIC (appelé par Stripe, sans JWT).
 */
@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    private final PaymentService paymentService;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handle(@RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            // Pas de webhook configuré : on ignore (la confirmation au retour suffit).
            return ResponseEntity.ok("ignored");
        }
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Optional<StripeObject> obj = event.getDataObjectDeserializer().getObject();
            if (obj.isPresent() && obj.get() instanceof Session session
                    && "paid".equalsIgnoreCase(session.getPaymentStatus())) {
                Map<String, String> md = session.getMetadata();
                if (md != null && md.get("leaseId") != null && md.get("period") != null) {
                    try {
                        Long leaseId = Long.valueOf(md.get("leaseId"));
                        YearMonth period = YearMonth.from(LocalDate.parse(md.get("period")));
                        paymentService.recordPaidPaymentByLeaseId(leaseId, period);
                    } catch (Exception ignored) {
                        // métadonnées inattendues : on ignore sans faire échouer le webhook.
                    }
                }
            }
        }
        return ResponseEntity.ok("ok");
    }
}
