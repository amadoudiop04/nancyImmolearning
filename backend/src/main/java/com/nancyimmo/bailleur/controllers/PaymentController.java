package com.nancyimmo.bailleur.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.nancyimmo.bailleur.dto.PaymentDto;
import com.nancyimmo.bailleur.dto.PaymentStatsDto;
import com.nancyimmo.bailleur.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentDto create(@RequestBody PaymentDto dto) {
        return paymentService.create(dto);
    }

    @GetMapping
    public List<PaymentDto> getAll(
            @RequestParam(required = false) Long leaseId,
            @RequestParam(required = false) String status) {
        if (leaseId != null) return paymentService.findByLeaseId(leaseId);
        if (status != null) return paymentService.findByStatus(status);
        return paymentService.findAll();
    }

    @GetMapping("/stats")
    public PaymentStatsDto getStats() {
        return paymentService.getStats();
    }

    @GetMapping("/{id}")
    public PaymentDto getOne(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PutMapping("/{id}")
    public PaymentDto update(@PathVariable Long id, @RequestBody PaymentDto dto) {
        return paymentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        paymentService.delete(id);
    }
}
