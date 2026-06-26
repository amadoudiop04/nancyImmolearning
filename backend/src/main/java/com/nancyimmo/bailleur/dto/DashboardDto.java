package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;

public class DashboardDto {

    private int totalProperties;
    private int activeTenants;
    private BigDecimal monthlyRevenue;
    private long occupancyRate;

    public int getTotalProperties() { return totalProperties; }
    public void setTotalProperties(int totalProperties) { this.totalProperties = totalProperties; }

    public int getActiveTenants() { return activeTenants; }
    public void setActiveTenants(int activeTenants) { this.activeTenants = activeTenants; }

    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

    public long getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(long occupancyRate) { this.occupancyRate = occupancyRate; }
}
