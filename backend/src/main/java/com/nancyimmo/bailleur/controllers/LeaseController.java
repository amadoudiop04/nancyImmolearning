package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.nancyimmo.bailleur.services.LeaseService;
import com.nancyimmo.bailleur.dto.LeaseDto;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/leases")
public class LeaseController {
    private final LeaseService leaseService;

    public LeaseController(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @PostMapping("")
    public void create(@RequestBody LeaseDto leaseDto) {
        leaseService.create(leaseDto);
    }

    @GetMapping("")
    public List<LeaseDto> getAll() {
        return leaseService.findAll();
    }

    @GetMapping("/{id}")
    public LeaseDto getOne(@PathVariable Long id) {
        return leaseService.findById(id);
    }

    @PutMapping("/{id}")
    public LeaseDto update(@PathVariable Long id, @RequestBody LeaseDto leaseDto) {
        return leaseService.update(id, leaseDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        leaseService.delete(id);
    }

}
