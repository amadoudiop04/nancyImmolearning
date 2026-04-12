package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.nancyimmo.bailleur.dto.PropertyDto;
import com.nancyimmo.bailleur.services.PropertyService;

@RestController
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

    @GetMapping("/{id}")
    public PropertyDto getOne(@PathVariable Long id) {
        return propertyService.findById(id);
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