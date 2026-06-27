package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Un mois de loyer dû mais non réglé (arriéré en retard ou mois courant à payer). */
public class DueMonthDto {

    private LocalDate period;   // 1er jour du mois concerné
    private String label;       // ex. "Mars 2026"
    private BigDecimal amount;  // loyer + charges dus
    private String status;      // LATE (en retard) | CURRENT (mois courant)

    public DueMonthDto() {
    }

    public DueMonthDto(LocalDate period, String label, BigDecimal amount, String status) {
        this.period = period;
        this.label = label;
        this.amount = amount;
        this.status = status;
    }

    public LocalDate getPeriod() { return period; }
    public void setPeriod(LocalDate period) { this.period = period; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
