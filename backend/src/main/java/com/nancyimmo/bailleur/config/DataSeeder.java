package com.nancyimmo.bailleur.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.nancyimmo.bailleur.models.ApplicationModel;
import com.nancyimmo.bailleur.models.BuildingModel;
import com.nancyimmo.bailleur.models.DocumentModel;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PaymentModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.models.TenantModel;
import com.nancyimmo.bailleur.repositories.ApplicationRepository;
import com.nancyimmo.bailleur.repositories.BuildingRepository;
import com.nancyimmo.bailleur.repositories.DocumentRepository;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.repositories.LeaseRepository;
import com.nancyimmo.bailleur.repositories.PaymentRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;
import com.nancyimmo.bailleur.repositories.TenantRepository;
import com.nancyimmo.bailleur.services.PdfService;

/**
 * Pré-remplit la base avec un jeu de données démo cohérent au premier démarrage
 * (uniquement si aucun bailleur n'existe). Permet d'utiliser l'application
 * immédiatement. Compte démo : nancy@nancyimmo.fr / password123.
 */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private static final String IMG = "https://images.unsplash.com/";
    private static final String IMG_OPTS = "?auto=format&fit=crop&w=900&q=60";

    private final LandlordRepository landlordRepository;
    private final BuildingRepository buildingRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentRepository paymentRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final PdfService pdfService;

    // Bailleur courant en cours d'amorçage (pour rattacher immeubles/locataires/documents).
    private LandlordModel seedLandlord;

    public DataSeeder(LandlordRepository landlordRepository,
            BuildingRepository buildingRepository,
            PropertyRepository propertyRepository,
            TenantRepository tenantRepository,
            LeaseRepository leaseRepository,
            PaymentRepository paymentRepository,
            DocumentRepository documentRepository,
            ApplicationRepository applicationRepository,
            PasswordEncoder passwordEncoder,
            PdfService pdfService) {
        this.landlordRepository = landlordRepository;
        this.buildingRepository = buildingRepository;
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.leaseRepository = leaseRepository;
        this.paymentRepository = paymentRepository;
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
        this.pdfService = pdfService;
    }

    @Override
    public void run(String... args) {
        if (landlordRepository.count() > 0) {
            return; // Base déjà initialisée : on ne réinsère rien.
        }

        // --- Bailleur (= compte de connexion) ---
        LandlordModel nancy = new LandlordModel();
        nancy.setFirstName("Nancy");
        nancy.setLastName("Aubert");
        nancy.setEmail("nancy@nancyimmo.fr");
        nancy.setPassword(passwordEncoder.encode("password123"));
        nancy.setPhone("0612345678");
        nancy.setStreet("8 Rue de la Source");
        nancy.setCity("Nancy");
        nancy.setZipCode("54000");
        nancy.setCountry("France");
        nancy = landlordRepository.save(nancy);
        this.seedLandlord = nancy;

        // --- Immeubles ---
        BuildingModel lilas = building("Résidence Les Lilas", "12 Avenue Foch", "Nancy", "54000");
        BuildingModel stanislas = building("Carré Stanislas", "4 Place Stanislas", "Nancy", "54000");

        // --- Locataires ---
        TenantModel thomas = tenant("Thomas", "Bernard", "thomas.bernard@email.fr", "0612001122",
                "12 Avenue Foch", "Nancy", "54000");
        // Compte locataire démo (accès à l'espace locataire) : thomas.bernard@email.fr / password123
        thomas.setPassword(passwordEncoder.encode("password123"));
        thomas = tenantRepository.save(thomas);
        TenantModel camille = tenant("Camille", "Roussel", "camille.roussel@email.fr", "0612003344",
                "18 Rue du Sergent Blandan", "Nancy", "54000");
        TenantModel marie = tenant("Marie", "Lefebvre", "marie.lefebvre@email.fr", "0612005566",
                "9 Rue Carnot", "Nancy", "54000");

        // --- Biens occupés ---
        PropertyModel pFoch = property("Appartement T4 Foch", "T4", "85 m²",
                "12 Avenue Foch, Nancy", new BigDecimal("1450"), lilas, nancy,
                "Bel appartement familial au 3ᵉ étage avec balcon, double séjour et place de parking.",
                IMG + "photo-1502672260266-1c1ef2d93688" + IMG_OPTS);
        PropertyModel pDuplex = property("Duplex Sainte-Marie", "T3", "62 m²",
                "18 Rue du Sergent Blandan, Nancy", new BigDecimal("1150"), lilas, nancy,
                "Duplex lumineux proche du parc Sainte-Marie, cuisine équipée et mezzanine.",
                IMG + "photo-1493809842364-78817add7ffb" + IMG_OPTS);
        PropertyModel pCarnot = property("Appartement Carnot", "T2", "45 m²",
                "9 Rue Carnot, Nancy", new BigDecimal("820"), stanislas, nancy,
                "T2 rénové en plein centre, idéal jeune actif, proche commerces et tram.",
                IMG + "photo-1560448204-e02f11c3d0e2" + IMG_OPTS);

        // --- Biens disponibles ---
        PropertyModel pStudio = property("Studio Stanislas", "T1", "22 m²",
                "4 Place Stanislas, Nancy", new BigDecimal("680"), stanislas, nancy,
                "Studio meublé sur la plus belle place de Nancy, parfait pour étudiant.",
                IMG + "photo-1522708323590-d24dbb6b0267" + IMG_OPTS);
        PropertyModel pVillers = property("Maison Villers", "T5", "120 m²",
                "24 Rue de la Paix, Villers-lès-Nancy", new BigDecimal("1850"), null, nancy,
                "Grande maison avec jardin et garage, quartier résidentiel calme.",
                IMG + "photo-1568605114967-8130f3a36994" + IMG_OPTS);
        PropertyModel pJeanne = property("T2 Jeanne d'Arc", "T2", "48 m²",
                "31 Rue Jeanne d'Arc, Nancy", new BigDecimal("760"), null, nancy,
                "Appartement clair avec cave, à deux pas de la gare.",
                IMG + "photo-1484154218962-a197022b5858" + IMG_OPTS);

        // --- Baux (relient bien occupé ↔ locataire) ---
        LeaseModel lFoch = lease(pFoch, thomas, LocalDate.now().minusMonths(6), new BigDecimal("1450"));
        LeaseModel lDuplex = lease(pDuplex, camille, LocalDate.now().minusMonths(4), new BigDecimal("1150"));
        LeaseModel lCarnot = lease(pCarnot, marie, LocalDate.now().minusMonths(8), new BigDecimal("820"));

        // --- Paiements (crédits du relevé de compte) ---
        seedPayments(lFoch, new BigDecimal("1450"), 5, false);
        seedPayments(lDuplex, new BigDecimal("1150"), 3, false);
        seedPayments(lCarnot, new BigDecimal("820"), 7, true); // dernier mois en retard

        // --- Documents (vrais PDF générés) ---
        leaseDoc("Contrat de bail — Appartement T4 Foch", "LEASE", lFoch, pdfService.buildBail(lFoch));
        leaseDoc("Quittance — Foch — " + monthLabel(0), "QUITTANCE", lFoch,
                pdfService.buildQuittance(lFoch, java.time.YearMonth.now(), lFoch.getRentAmount()));
        leaseDoc("Contrat de bail — Duplex Sainte-Marie", "LEASE", lDuplex, pdfService.buildBail(lDuplex));
        leaseDoc("Quittance — Carnot — " + monthLabel(1), "QUITTANCE", lCarnot,
                pdfService.buildQuittance(lCarnot, java.time.YearMonth.now().minusMonths(1), lCarnot.getRentAmount()));

        // --- Candidatures (dossiers déposés sur les biens disponibles) ---
        application(pStudio, "Julie", "Moreau", "julie.moreau@email.fr", "0623456789",
                "Étudiante en master, revenus garantis par mes parents (garants).");
        application(pVillers, "Karim", "Benali", "karim.benali@email.fr", "0698765432",
                "Famille avec deux enfants, CDI, 3,5x le loyer en revenus.");

        System.out.println("Données démo insérées — bailleur : nancy@nancyimmo.fr / password123 ; "
                + "locataire : thomas.bernard@email.fr / password123.");
    }

    // ---- Helpers ----

    private BuildingModel building(String name, String street, String city, String zip) {
        BuildingModel b = new BuildingModel();
        b.setName(name);
        b.setStreet(street);
        b.setCity(city);
        b.setZipCode(zip);
        b.setCountry("France");
        b.setLandlord(seedLandlord);
        return buildingRepository.save(b);
    }

    private TenantModel tenant(String first, String last, String email, String phone,
            String street, String city, String zip) {
        TenantModel t = new TenantModel();
        t.setFirstName(first);
        t.setLastName(last);
        t.setEmail(email);
        t.setPhone(phone);
        t.setStreet(street);
        t.setCity(city);
        t.setZipCode(zip);
        t.setCountry("France");
        t.setLandlord(seedLandlord);
        return tenantRepository.save(t);
    }

    private PropertyModel property(String name, String kind, String size, String location,
            BigDecimal rent, BuildingModel building, LandlordModel landlord,
            String description, String imageUrl) {
        PropertyModel p = new PropertyModel();
        p.setName(name);
        p.setKind(kind);
        p.setSize(size);
        p.setLocation(location);
        p.setRent(rent);
        p.setDescription(description);
        p.setImageUrl(imageUrl);
        p.setBuilding(building);
        p.setLandlord(landlord);
        return propertyRepository.save(p);
    }

    private LeaseModel lease(PropertyModel property, TenantModel tenant, LocalDate start, BigDecimal rent) {
        LeaseModel l = new LeaseModel();
        l.setProperty(property);
        l.setTenant(tenant);
        l.setSignatureDate(start.minusDays(7));
        l.setStartDate(start);
        l.setEndDate(start.plusYears(3));
        l.setRentAmount(rent);
        l.setCurrency("EUR");
        return leaseRepository.save(l);
    }

    private void seedPayments(LeaseModel lease, BigDecimal amount, int months, boolean lastLate) {
        for (int i = months; i >= 0; i--) {
            PaymentModel pay = new PaymentModel();
            pay.setLease(lease);
            LocalDate period = LocalDate.now().minusMonths(i).withDayOfMonth(5);
            pay.setPeriod(period);
            pay.setAmount(amount);
            if (i == 0 && lastLate) {
                pay.setStatus("LATE");
            } else {
                pay.setStatus("PAID");
                pay.setPaidDate(period);
            }
            paymentRepository.save(pay);
        }
    }

    private void leaseDoc(String name, String type, LeaseModel lease, byte[] content) {
        DocumentModel d = new DocumentModel();
        d.setName(name);
        d.setDocType(type);
        d.setCreatedAt(LocalDate.now());
        d.setLandlord(seedLandlord);
        d.setProperty(lease.getProperty());
        d.setTenant(lease.getTenant());
        d.setContent(content);
        d.setContentType("application/pdf");
        d.setFileName(name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "") + ".pdf");
        documentRepository.save(d);
    }

    private void application(PropertyModel property, String first, String last,
            String email, String phone, String message) {
        ApplicationModel a = new ApplicationModel();
        a.setProperty(property);
        a.setFirstName(first);
        a.setLastName(last);
        a.setEmail(email);
        a.setPhone(phone);
        a.setMessage(message);
        a.setStatus("PENDING");
        a.setCreatedAt(LocalDate.now().minusDays(2));
        applicationRepository.save(a);
    }

    private String monthLabel(int monthsAgo) {
        LocalDate d = LocalDate.now().minusMonths(monthsAgo);
        return d.format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
    }
}
