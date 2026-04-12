package com.nancyimmo.bailleur.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nancyimmo.bailleur.models.BuildingModel;
import com.nancyimmo.bailleur.repositories.BuildingRepository;

@RestController
@RequestMapping("/api")
public class BuildingController {
    @Autowired
    private BuildingRepository buildingRepository;

    @PostMapping("/building")
    public BuildingModel addBuilding(@RequestBody BuildingModel building) {
        return buildingRepository.save(building);
    }

    @GetMapping("/buildings")
    public Iterable<BuildingModel> getAllBuildings() {
        return buildingRepository.findAll();
    }

    // building by id
    @GetMapping("/building/{id}")
    public BuildingModel getBuilding(@PathVariable Long id) {
        return buildingRepository.findById(id).orElse(null);
    }

    // update building by id
    @PutMapping("/building/{id}")
    public BuildingModel updateBuilding(@PathVariable Long id, @RequestBody BuildingModel building) {
        building.setId(id);
        return buildingRepository.save(building);
    }

    // delete building by id
    @DeleteMapping("/building/{id}")
    public void deleteBuilding(@PathVariable Long id) {
        buildingRepository.deleteById(id);
    }
}