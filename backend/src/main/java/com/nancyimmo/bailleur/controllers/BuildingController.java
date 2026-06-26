package com.nancyimmo.bailleur.controllers;

import java.util.List;

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
import com.nancyimmo.bailleur.security.CurrentUser;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingRepository buildingRepository;
    private final CurrentUser currentUser;

    public BuildingController(BuildingRepository buildingRepository, CurrentUser currentUser) {
        this.buildingRepository = buildingRepository;
        this.currentUser = currentUser;
    }

    @PostMapping()
    public BuildingModel addBuilding(@RequestBody BuildingModel building) {
        // L'immeuble appartient au bailleur connecté.
        building.setId(null);
        building.setLandlord(currentUser.requireLandlord());
        return buildingRepository.save(building);
    }

    @GetMapping()
    public List<BuildingModel> getAllBuildings() {
        return buildingRepository.findByLandlord_Email(currentUser.requireEmail());
    }

    // building by id
    @GetMapping("/{id}")
    public BuildingModel getBuilding(@PathVariable Long id) {
        return buildingRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail()).orElse(null);
    }

    // update building by id
    @PutMapping("/{id}")
    public BuildingModel updateBuilding(@PathVariable Long id, @RequestBody BuildingModel building) {
        return buildingRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .map(existing -> {
                    existing.setName(building.getName());
                    existing.setStreet(building.getStreet());
                    existing.setCity(building.getCity());
                    existing.setZipCode(building.getZipCode());
                    existing.setCountry(building.getCountry());
                    return buildingRepository.save(existing);
                })
                .orElse(null);
    }

    // delete building by id
    @DeleteMapping("/{id}")
    public void deleteBuilding(@PathVariable Long id) {
        buildingRepository.findByIdAndLandlord_Email(id, currentUser.requireEmail())
                .ifPresent(b -> buildingRepository.deleteById(id));
    }
}
