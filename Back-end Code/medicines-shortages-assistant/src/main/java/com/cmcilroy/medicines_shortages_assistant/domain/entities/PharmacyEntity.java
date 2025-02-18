package com.cmcilroy.medicines_shortages_assistant.domain.entities;

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

    private String eircode;

    // phone number is a String as opposed to an Integer to allow for non-numeric characters such as (), +, whitespace etc
    @Column(name = "phone_no")
    private String phoneNo;
    
}
