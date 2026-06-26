package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentDto {

    private Long id;
    private Long leaseId;
    private LocalDate period;
    private BigDecimal amount;
    private String status;
    private LocalDate paidDate;
    private String tenantName;
    private String propertyName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeaseId() { return leaseId; }
    public void setLeaseId(Long leaseId) { this.leaseId = leaseId; }

    public LocalDate getPeriod() { return period; }
    public void setPeriod(LocalDate period) { this.period = period; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
}
