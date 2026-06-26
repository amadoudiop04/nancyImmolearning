package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StatementLineDto {

    private LocalDate date;
    private String label;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balance;

    public StatementLineDto() {
    }

    public StatementLineDto(LocalDate date, String label, BigDecimal debit, BigDecimal credit, BigDecimal balance) {
        this.date = date;
        this.label = label;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }

    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
