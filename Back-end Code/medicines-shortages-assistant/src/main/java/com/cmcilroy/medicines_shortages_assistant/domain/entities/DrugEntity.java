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
@Table(name = "drugs")
public class DrugEntity {

    // Id annotation identifies Primary Key field
    @Id
    // for the drugs table this is going to be the product licence number
    // use Column annotation to specify what the corresponding column name in the database should be
    @Column(name = "licence_no")
    private String licenceNo;

    @Column(name = "product_name")
    private String productName;

    private String strength;

    @Column(name = "dosage_form")
    private String dosageForm;

    @Column(name = "active_substance", length = 1000)
    private String activeSubstance;

    @Column(name = "is_available")
    private Boolean isAvailable;
    
}
