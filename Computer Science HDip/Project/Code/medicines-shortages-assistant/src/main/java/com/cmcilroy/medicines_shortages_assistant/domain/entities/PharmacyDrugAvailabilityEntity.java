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
@Table(name = "pharmacy_drug_availabilities")
public class PharmacyDrugAvailabilityEntity {

    // the Primary Key in this case will be an automatically generated Id which will increment by one with each entry
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pharmacy_drug_availabilities_id_seq")
    private Long id;

    // many drug availabilities can be associated with one pharmacy
    @ManyToOne
    // name of the column in the database which connects to the pharmacies table
    @JoinColumn(name = "pharmacy_id", referencedColumnName = "psi_registration_no")
    private PharmacyEntity pharmacy;

    // many drug availabilities can be associated with one drug
    @ManyToOne
    // name of the column in the database which connects to the drugs table
    @JoinColumn(name = "drug_id", referencedColumnName = "licence_no")
    private DrugEntity drug;

    // use Column annotation to specify what the corresponding column name in the database should be
    // the values for this column should not be null as there should only be a record if the pharmacy adds it to reflect their stock availability
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

}
