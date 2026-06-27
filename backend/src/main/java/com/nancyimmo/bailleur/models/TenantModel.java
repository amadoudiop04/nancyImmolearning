package com.nancyimmo.bailleur.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "tenants")

public class TenantModel {


    @OneToMany(mappedBy = "tenant")
    private List<LeaseModel> leases;

    // Le bailleur propriétaire de la fiche locataire (isolation des données).
    @ManyToOne
    @JoinColumn(name = "landlord_id")
    private LandlordModel landlord;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @Column(name = "phone_number")
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String country;

    // Authentification : un locataire peut disposer d'un compte (mot de passe haché BCrypt).
    private String password;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private java.time.Instant resetTokenExpiry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<LeaseModel> getLeases() {
        return leases;
    }

    public void setLeases(List<LeaseModel> leases) {
        this.leases = leases;
    }

    public LandlordModel getLandlord() {
        return landlord;
    }

    public void setLandlord(LandlordModel landlord) {
        this.landlord = landlord;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public java.time.Instant getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(java.time.Instant resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

}
