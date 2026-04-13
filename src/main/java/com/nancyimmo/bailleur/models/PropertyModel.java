package com.nancyimmo.bailleur.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Column;
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

    private String description;

    @JsonAlias("type_property")
    @Column(name = "type_property")
    private String typeProperty;

    private float price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeProperty() {
        return typeProperty;
    }

    public void setTypeProperty(String typeProperty) {
        this.typeProperty = typeProperty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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
