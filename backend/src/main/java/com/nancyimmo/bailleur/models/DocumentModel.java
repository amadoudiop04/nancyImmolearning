package com.nancyimmo.bailleur.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documents")
public class DocumentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String docType;
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = true)
    private PropertyModel property;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = true)
    private TenantModel tenant;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public PropertyModel getProperty() { return property; }
    public void setProperty(PropertyModel property) { this.property = property; }

    public TenantModel getTenant() { return tenant; }
    public void setTenant(TenantModel tenant) { this.tenant = tenant; }
}
