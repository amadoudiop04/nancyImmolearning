package com.nancyimmo.bailleur.dto;

import java.math.BigDecimal;

public class PropertyDto {

    private Long id;
    private String name;
    private String size;
    private String kind;
    private String location;
    private String description;
    private BigDecimal rent;
    private Long buildingId;
    private Long landlordId;

    public PropertyDto() {
    }

    public PropertyDto(Long id, String name, String size, String kind, String location) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.kind = kind;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getRent() { return rent; }
    public void setRent(BigDecimal rent) { this.rent = rent; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public Long getLandlordId() { return landlordId; }
    public void setLandlordId(Long landlordId) { this.landlordId = landlordId; }
}
