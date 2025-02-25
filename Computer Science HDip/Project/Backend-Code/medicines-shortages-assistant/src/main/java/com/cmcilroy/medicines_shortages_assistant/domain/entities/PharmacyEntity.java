package com.cmcilroy.medicines_shortages_assistant.domain.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Entity annotation labels the Object as an entity which can be used with Spring Data JPA
@Entity
// Table annotation declares which table this maps to in the database
@Table(name = "pharmacies")
public class PharmacyEntity {

    // Id annotation identifies Primary Key field
    @Id
    // for the pharmacies table this is going to be the PSI registration no
    // use Column annotation to specify what the corresponding column name in the database should be
    @Column(name = "psi_registration_no")
    private Integer psiRegNo;

    @Column(name = "pharmacy_name")
    private String pharmacyName;

    private String address;

    private String eircode;

    // phone number is a String as opposed to an Integer to allow for non-numeric characters such as (), +, whitespace etc
    @Column(name = "phone_no")
    private String phoneNo;

    private String email;

    // use JsonIgnore to prevent password data from being exposed in API responses
    @JsonIgnore
    private String password;

    // fields responsible for account locking logic
    // use @Transient annotation to prevent inclusion in database table
    @Transient
    @Builder.Default
    private int attemptCounter = 0;

    @Transient
    @Builder.Default
    private boolean isLocked = false;

    @Transient
    @Builder.Default
    private LocalDateTime lockTimer = null;
    
    // method to unlock account
    public void unlockAccount() {
        this.attemptCounter = 0;
        this.isLocked = false;
        this.lockTimer = null;
    }

    // method to increment the attemptCounter and lock account
    public void incrementCounter() {
        this.attemptCounter++;
        // if 3 or more incorrect attempts have been made
        if (attemptCounter >= 3) {
            // lock account
            this.isLocked = true;
            // implement 5 minute timer
            this.lockTimer = LocalDateTime.now().plusMinutes(5);
        }
    }

}
