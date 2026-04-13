package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LeaseDto {

    private Long id;
    private LocalDate signatureDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rentAmount;
    private String currency;

    public LeaseDto() {
    }

    public LeaseDto(Long id, LocalDate signatureDate, LocalDate startDate, LocalDate endDate, BigDecimal rentAmount,
            String currency) {
        this.id = id;
        this.signatureDate = signatureDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    

}
