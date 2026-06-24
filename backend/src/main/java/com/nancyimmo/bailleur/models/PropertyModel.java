package com.nancyimmo.bailleur.models;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "properties")
public class PropertyModel {

    @OneToOne(mappedBy = "property")
    private LeaseModel lease;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private BuildingModel building;

    @ManyToOne
    @JoinColumn(name = "landlord_id")
    private LandlordModel landlord;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String size;
    private String kind;
    private String location;
    private String description;
    private BigDecimal rent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRent() {
        return rent;
    }

    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }

    public LeaseModel getLease() {
        return lease;
    }

    public void setLease(LeaseModel lease) {
        this.lease = lease;
    }

    public BuildingModel getBuilding() {
        return building;
    }

    public void setBuilding(BuildingModel building) {
        this.building = building;
    }

    public LandlordModel getLandlord() {
        return landlord;
    }

    public void setLandlord(LandlordModel landlord) {
        this.landlord = landlord;
    }



}
