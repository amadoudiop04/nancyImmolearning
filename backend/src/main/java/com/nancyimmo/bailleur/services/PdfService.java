package com.nancyimmo.bailleur.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nancyimmo.bailleur.models.LandlordModel;
import com.nancyimmo.bailleur.models.LeaseModel;
import com.nancyimmo.bailleur.models.PropertyModel;
import com.nancyimmo.bailleur.models.TenantModel;

/** Génère de vrais documents PDF (contrats de bail, quittances de loyer). */
@Service
public class PdfService {

    private static final Color BRAND = new Color(14, 79, 74);
    private static final Color MUTED = new Color(120, 130, 125);
    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat EUR = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    private Font font(String name, float size, Color color) {
        return FontFactory.getFont(name, size, color);
    }

    /** Contrat de bail d'habitation pour un bail (bailleur + locataire + bien). */
    public byte[] buildBail(LeaseModel lease) {
        PropertyModel property = lease.getProperty();
        TenantModel tenant = lease.getTenant();
        LandlordModel landlord = property != null ? property.getLandlord() : null;

        Document doc = new Document(PageSize.A4, 54, 54, 54, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        header(doc, "CONTRAT DE LOCATION");
        doc.add(spacer(10));

        doc.add(new Paragraph("Contrat de bail d'habitation soumis à la loi n° 89-462 du 6 juillet 1989.",
                font(FontFactory.HELVETICA, 9, MUTED)));
        doc.add(spacer(16));

        // Parties
        doc.add(section("1. Désignation des parties"));
        doc.add(kv("Bailleur",
                landlord != null ? fullName(landlord.getFirstName(), landlord.getLastName()) : "—"));
        if (landlord != null) {
            doc.add(kv("Adresse du bailleur", addr(landlord.getStreet(), landlord.getZipCode(), landlord.getCity(), landlord.getCountry())));
            doc.add(kv("Email", landlord.getEmail()));
        }
        doc.add(spacer(6));
        doc.add(kv("Locataire",
                tenant != null ? fullName(tenant.getFirstName(), tenant.getLastName()) : "—"));
        if (tenant != null) {
            doc.add(kv("Adresse du locataire", addr(tenant.getStreet(), tenant.getZipCode(), tenant.getCity(), tenant.getCountry())));
            doc.add(kv("Email", tenant.getEmail()));
        }
        doc.add(spacer(14));

        // Bien
        doc.add(section("2. Objet du contrat — Logement loué"));
        if (property != null) {
            doc.add(kv("Désignation", property.getName()));
            doc.add(kv("Type / Surface", nullSafe(property.getKind()) + " · " + nullSafe(property.getSize())));
            doc.add(kv("Adresse", nullSafe(property.getLocation())));
            if (property.getDescription() != null && !property.getDescription().isBlank()) {
                doc.add(kv("Descriptif", property.getDescription()));
            }
        }
        doc.add(spacer(14));

        // Conditions financières
        doc.add(section("3. Durée et conditions financières"));
        doc.add(kv("Date de signature", lease.getSignatureDate() != null ? lease.getSignatureDate().format(DATE_FR) : "—"));
        doc.add(kv("Prise d'effet", lease.getStartDate() != null ? lease.getStartDate().format(DATE_FR) : "—"));
        doc.add(kv("Échéance", lease.getEndDate() != null ? lease.getEndDate().format(DATE_FR) : "—"));
        doc.add(kv("Loyer mensuel (charges comprises)", money(lease.getRentAmount())));
        doc.add(spacer(16));

        doc.add(new Paragraph(
                "Le présent contrat est établi entre les parties désignées ci-dessus. Le locataire "
                + "s'engage à payer le loyer aux échéances convenues et à user paisiblement du logement. "
                + "Le bailleur s'engage à délivrer un logement décent et à en assurer la jouissance paisible.",
                font(FontFactory.HELVETICA, 10, Color.DARK_GRAY)));
        doc.add(spacer(28));

        // Signatures
        signatures(doc,
                landlord != null ? fullName(landlord.getFirstName(), landlord.getLastName()) : "Le bailleur",
                tenant != null ? fullName(tenant.getFirstName(), tenant.getLastName()) : "Le locataire");

        footer(doc);
        doc.close();
        return baos.toByteArray();
    }

    /** Quittance de loyer pour un mois donné. */
    public byte[] buildQuittance(LeaseModel lease, YearMonth month, BigDecimal amount) {
        PropertyModel property = lease.getProperty();
        TenantModel tenant = lease.getTenant();
        LandlordModel landlord = property != null ? property.getLandlord() : null;
        String monthLabel = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));

        Document doc = new Document(PageSize.A4, 54, 54, 54, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        header(doc, "QUITTANCE DE LOYER");
        doc.add(spacer(6));
        doc.add(new Paragraph(monthLabel.substring(0, 1).toUpperCase() + monthLabel.substring(1),
                font(FontFactory.HELVETICA_BOLD, 13, BRAND)));
        doc.add(spacer(16));

        doc.add(section("Bailleur"));
        doc.add(new Paragraph(landlord != null ? fullName(landlord.getFirstName(), landlord.getLastName()) : "—",
                font(FontFactory.HELVETICA, 11, Color.BLACK)));
        doc.add(spacer(10));
        doc.add(section("Locataire"));
        doc.add(new Paragraph(tenant != null ? fullName(tenant.getFirstName(), tenant.getLastName()) : "—",
                font(FontFactory.HELVETICA, 11, Color.BLACK)));
        doc.add(spacer(10));
        doc.add(section("Logement"));
        doc.add(new Paragraph(property != null ? property.getName() + " — " + nullSafe(property.getLocation()) : "—",
                font(FontFactory.HELVETICA, 11, Color.BLACK)));
        doc.add(spacer(18));

        // Encadré montant
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(
                "Reçu la somme de " + money(amount) + " au titre du loyer et des charges pour " + monthLabel + ".",
                font(FontFactory.HELVETICA_BOLD, 11, BRAND)));
        cell.setBackgroundColor(new Color(231, 241, 239));
        cell.setBorderColor(new Color(207, 224, 218));
        cell.setPadding(14);
        box.addCell(cell);
        doc.add(box);
        doc.add(spacer(16));

        doc.add(new Paragraph(
                "Je soussigné(e) bailleur déclare avoir reçu du locataire le montant ci-dessus et lui en "
                + "donne quittance pour la période concernée, sous réserve de tous mes droits. "
                + "Cette quittance annule tous les reçus qui auraient pu être établis précédemment pour la même période.",
                font(FontFactory.HELVETICA, 10, Color.DARK_GRAY)));
        doc.add(spacer(10));
        doc.add(kv("Fait le", LocalDate.now().format(DATE_FR)));
        doc.add(spacer(24));

        signatures(doc,
                landlord != null ? fullName(landlord.getFirstName(), landlord.getLastName()) : "Le bailleur",
                null);

        footer(doc);
        doc.close();
        return baos.toByteArray();
    }

    // ─── Helpers de mise en page ──────────────────────────────────────────────

    private void header(Document doc, String title) {
        Paragraph brand = new Paragraph("Nancy Immo", font(FontFactory.HELVETICA_BOLD, 20, BRAND));
        doc.add(brand);
        Paragraph sub = new Paragraph("Gestion locative autonome", font(FontFactory.HELVETICA, 9, MUTED));
        doc.add(sub);
        doc.add(spacer(14));
        Paragraph t = new Paragraph(title, font(FontFactory.HELVETICA_BOLD, 16, Color.BLACK));
        doc.add(t);
        // ligne de séparation
        PdfPTable rule = new PdfPTable(1);
        rule.setWidthPercentage(100);
        PdfPCell c = new PdfPCell();
        c.setFixedHeight(3f);
        c.setBackgroundColor(BRAND);
        c.setBorder(0);
        rule.addCell(c);
        doc.add(spacer(6));
        doc.add(rule);
    }

    private Paragraph section(String label) {
        Paragraph p = new Paragraph(label, font(FontFactory.HELVETICA_BOLD, 12, BRAND));
        p.setSpacingBefore(4);
        p.setSpacingAfter(6);
        return p;
    }

    private Paragraph kv(String key, String value) {
        Phrase phrase = new Phrase();
        phrase.add(new com.lowagie.text.Chunk(key + " : ", font(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY)));
        phrase.add(new com.lowagie.text.Chunk(nullSafe(value), font(FontFactory.HELVETICA, 10, Color.BLACK)));
        Paragraph p = new Paragraph(phrase);
        p.setSpacingAfter(3);
        return p;
    }

    private void signatures(Document doc, String left, String right) {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.addCell(sigCell("Le bailleur", left));
        t.addCell(right != null ? sigCell("Le locataire", right) : emptyCell());
        doc.add(t);
    }

    private PdfPCell sigCell(String role, String name) {
        Phrase phrase = new Phrase();
        phrase.add(new com.lowagie.text.Chunk(role + "\n", font(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY)));
        phrase.add(new com.lowagie.text.Chunk(nullSafe(name) + "\n\n\n", font(FontFactory.HELVETICA, 10, Color.BLACK)));
        phrase.add(new com.lowagie.text.Chunk("Signature : ____________________", font(FontFactory.HELVETICA, 9, MUTED)));
        PdfPCell c = new PdfPCell(phrase);
        c.setBorder(0);
        c.setPaddingTop(8);
        return c;
    }

    private PdfPCell emptyCell() {
        PdfPCell c = new PdfPCell(new Phrase(""));
        c.setBorder(0);
        return c;
    }

    private void footer(Document doc) {
        doc.add(spacer(22));
        Paragraph f = new Paragraph(
                "Document généré automatiquement via Nancy Immo le " + LocalDate.now().format(DATE_FR) + ".",
                font(FontFactory.HELVETICA, 8, MUTED));
        f.setAlignment(Element.ALIGN_CENTER);
        doc.add(f);
    }

    private Paragraph spacer(float height) {
        Paragraph p = new Paragraph(" ");
        p.setLeading(height);
        return p;
    }

    private String money(BigDecimal amount) {
        return amount != null ? EUR.format(amount) : "—";
    }

    private String fullName(String first, String last) {
        return (nullSafe(first) + " " + nullSafe(last)).trim();
    }

    private String addr(String street, String zip, String city, String country) {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isBlank()) sb.append(street);
        if (zip != null && !zip.isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(zip);
        if (city != null && !city.isBlank()) sb.append(" ").append(city);
        if (country != null && !country.isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(country);
        return sb.length() > 0 ? sb.toString() : "—";
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }
}
