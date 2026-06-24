package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;

public class PaymentStatsDto {

    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private BigDecimal totalLate;

    public PaymentStatsDto(BigDecimal totalPaid, BigDecimal totalPending, BigDecimal totalLate) {
        this.totalPaid = totalPaid;
        this.totalPending = totalPending;
        this.totalLate = totalLate;
    }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public BigDecimal getTotalPending() { return totalPending; }
    public void setTotalPending(BigDecimal totalPending) { this.totalPending = totalPending; }

    public BigDecimal getTotalLate() { return totalLate; }
    public void setTotalLate(BigDecimal totalLate) { this.totalLate = totalLate; }
}
