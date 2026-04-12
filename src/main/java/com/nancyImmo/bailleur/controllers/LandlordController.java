package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.nancyimmo.bailleur.dto.LandlordDto;
import com.nancyimmo.bailleur.services.LandlordService;

@RestController
@RequestMapping("/api/landlords")
public class LandlordController {

    private final LandlordService landlordService;

    public LandlordController(LandlordService landlordService) {
        this.landlordService = landlordService;
    }

    @PostMapping("")
    public LandlordDto create(@RequestBody LandlordDto landlordDto) {
        return landlordService.createLandlord(landlordDto);
    }

    @GetMapping("")
    public List<LandlordDto> getAll() {
        return landlordService.findAll();
    }

    @GetMapping("/{id}")
    public LandlordDto getOne(@RequestParam Long id) {
        return landlordService.findById(id);

    }

    @PutMapping("/{id}")
    public LandlordDto update(@PathVariable Long id, @RequestBody LandlordDto landlordDto) {
        return landlordService.update(id, landlordDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        landlordService.delete(id);
    }

}
