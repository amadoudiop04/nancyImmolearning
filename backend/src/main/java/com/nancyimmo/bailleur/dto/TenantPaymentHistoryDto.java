package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Historique de paiement d'un locataire sur une année : une case par mois
 * (encaissé / en attente / en retard / hors bail) + totaux annuels.
 */
public class TenantPaymentHistoryDto {

    private Long tenantId;
    private String tenantName;
    private String propertyName;
    private int year;
    private List<MonthCell> months;
    private BigDecimal totalEncaisse = BigDecimal.ZERO;
    private BigDecimal totalEnAttente = BigDecimal.ZERO;
    private BigDecimal totalRetard = BigDecimal.ZERO;

    public static class MonthCell {
        private int month;          // 1..12
        private String label;       // "Janv.", "Févr.", ...
        private String status;      // PAID | PENDING | LATE | NONE
        private BigDecimal amount;  // montant dû ou encaissé pour le mois
        private LocalDate paidDate; // date d'encaissement (si payé)

        public MonthCell() {}

        public MonthCell(int month, String label, String status, BigDecimal amount, LocalDate paidDate) {
            this.month = month;
            this.label = label;
            this.status = status;
            this.amount = amount;
            this.paidDate = paidDate;
        }

        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public LocalDate getPaidDate() { return paidDate; }
        public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }
    }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public List<MonthCell> getMonths() { return months; }
    public void setMonths(List<MonthCell> months) { this.months = months; }
    public BigDecimal getTotalEncaisse() { return totalEncaisse; }
    public void setTotalEncaisse(BigDecimal totalEncaisse) { this.totalEncaisse = totalEncaisse; }
    public BigDecimal getTotalEnAttente() { return totalEnAttente; }
    public void setTotalEnAttente(BigDecimal totalEnAttente) { this.totalEnAttente = totalEnAttente; }
    public BigDecimal getTotalRetard() { return totalRetard; }
    public void setTotalRetard(BigDecimal totalRetard) { this.totalRetard = totalRetard; }
}
