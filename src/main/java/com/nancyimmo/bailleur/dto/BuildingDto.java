package com.nancyimmo.bailleur.dto;

public class BuildingDto {

    private Long id;
    private String name;
    private String street;
    private String city;
    private String zipcode;
    private String country;

    public BuildingDto() {
    }

    public BuildingDto(Long id, String name, String street, String city, String zipcode, String country) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
        this.country = country;
    }

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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
