package com.nancyimmo.bailleur.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lease_id")
    private LeaseModel lease;

    private LocalDate period;
    private BigDecimal amount;
    private String status;
    private LocalDate paidDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LeaseModel getLease() { return lease; }
    public void setLease(LeaseModel lease) { this.lease = lease; }

    public LocalDate getPeriod() { return period; }
    public void setPeriod(LocalDate period) { this.period = period; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }
}
