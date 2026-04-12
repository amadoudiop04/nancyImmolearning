package com.nancyimmo.bailleur.dto;

public class PropertyDto {

    private Long id;
    private String description;
    private String typeProperty;
    private float price;

    public PropertyDto() {}

    public PropertyDto(Long id, String description, String typeProperty, float price) {
        this.id = id;
        this.description = description;
        this.typeProperty = typeProperty;
        this.price = price;
    }

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
}