package com.nancyImmo.bailleur.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nancyImmo.bailleur.models.BuildingModel;
import com.nancyImmo.bailleur.repositories.BuildingRepository;

@RestController
public class BuildingController {
    @Autowired
    private BuildingRepository buildingRepository;

    @PostMapping("/api/buildings")
    public BuildingModel addBuilding(@RequestBody BuildingModel building) {
        return buildingRepository.save(building);
    }

    @GetMapping("/api/buildings")
    public Iterable<BuildingModel> getAllBuildings() {
        return buildingRepository.findAll();
    }

    // building by id
    @GetMapping("/api/buildings/{id}")
    public BuildingModel getBuilding(@PathVariable Long id) {
        return buildingRepository.findById(id).orElse(null);
    }

    // update building by id
    @PutMapping("/api/buildings/{id}")
    public BuildingModel updateBuilding(@PathVariable Long id, @RequestBody BuildingModel building) {
        building.setId(id);
        return buildingRepository.save(building);
    }

    // delete building by id
    @DeleteMapping("/api/buildings/{id}")
    public void deleteBuilding(@PathVariable Long id) {
        buildingRepository.deleteById(id);
    }
}