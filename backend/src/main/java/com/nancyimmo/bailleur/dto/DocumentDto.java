package com.nancyimmo.bailleur.dto;

import java.time.LocalDate;

public class DocumentDto {

    private Long id;
    private String name;
    private String docType;
    private LocalDate createdAt;
    private Long propertyId;
    private Long tenantId;
    private boolean hasFile;
    private String fileName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isHasFile() { return hasFile; }
    public void setHasFile(boolean hasFile) { this.hasFile = hasFile; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
}
