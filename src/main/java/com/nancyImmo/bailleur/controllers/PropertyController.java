package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.nancyimmo.bailleur.dto.PropertyDto;
import com.nancyimmo.bailleur.services.PropertyService;

@RestController
@RequestMapping("/api")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/property")
    public PropertyDto create(@RequestBody PropertyDto dto) {
        return propertyService.create(dto);
    }

    @GetMapping("/properties")
    public List<PropertyDto> getAll() {
        return propertyService.findAll();
    }

    @GetMapping("/property/{id}")
    public PropertyDto getOne(@PathVariable Long id) {
        return propertyService.findById(id);
    }

    @PutMapping("/property/{id}")
    public PropertyDto update(@PathVariable Long id, @RequestBody PropertyDto dto) {
        return propertyService.update(id, dto);
    }

    @DeleteMapping("/property/{id}")
    public void delete(@PathVariable Long id) {
        propertyService.delete(id);
    }
}