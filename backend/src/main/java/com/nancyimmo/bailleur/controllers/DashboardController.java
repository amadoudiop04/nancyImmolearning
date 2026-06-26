package com.nancyimmo.bailleur.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.nancyimmo.bailleur.dto.DashboardDto;
import com.nancyimmo.bailleur.dto.PropertyDetailsDto;
import com.nancyimmo.bailleur.services.PropertyService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PropertyService propertyService;

    public DashboardController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public DashboardDto getStats() {
        List<PropertyDetailsDto> props = propertyService.findAllDetails();

        int totalProperties = props.size();
        long activeTenants = props.stream().filter(p -> p.getTenant() != null).count();
        BigDecimal monthlyRevenue = props.stream()
                .filter(p -> p.getLease() != null && p.getLease().getRentAmount() != null)
                .map(p -> p.getLease().getRentAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double rate = totalProperties > 0 ? (double) activeTenants / totalProperties * 100 : 0;

        DashboardDto dto = new DashboardDto();
        dto.setTotalProperties(totalProperties);
        dto.setActiveTenants((int) activeTenants);
        dto.setMonthlyRevenue(monthlyRevenue);
        dto.setOccupancyRate(Math.round(rate));
        return dto;
    }
}
