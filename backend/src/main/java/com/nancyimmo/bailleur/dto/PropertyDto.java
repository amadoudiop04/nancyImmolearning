package com.nancyimmo.bailleur.dto;

public class PropertyDto {

    private Long id;
    private String name;
    private String size;
    private String kind;
    private String location;

    public PropertyDto() {
    }

    public PropertyDto(Long id, String name, String size, String kind, String location) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.kind = kind;
        this.location = location;
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

}