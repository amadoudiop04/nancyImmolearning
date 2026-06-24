package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

import com.nancyimmo.bailleur.dto.PropertyDto;
import com.nancyimmo.bailleur.dto.PropertyDetailsDto;
import com.nancyimmo.bailleur.services.PropertyService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping()
    public PropertyDto create(@RequestBody PropertyDto dto) {
        return propertyService.create(dto);
    }

    @GetMapping()
    public List<PropertyDto> getAll() {
        return propertyService.findAll();
    }

    @GetMapping("/details")
    public List<PropertyDetailsDto> getAllWithDetails() {
        return propertyService.findAllDetails();
    }

    @GetMapping("/available")
    public List<PropertyDetailsDto> getAvailable() {
        return propertyService.findAvailable();
    }

    @GetMapping("/{id}")
    public PropertyDto getOne(@PathVariable Long id) {
        return propertyService.findById(id);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<PropertyDetailsDto> getOneWithDetails(@PathVariable Long id) {
        PropertyDetailsDto details = propertyService.findDetailsById(id);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @PutMapping("/{id}")
    public PropertyDto update(@PathVariable Long id, @RequestBody PropertyDto dto) {
        return propertyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        propertyService.delete(id);
    }
}