package com.cmcilroy.medicines_shortages_assistant.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrugDto {
    
    private String licenceNo;

    private String productName;

    private String manufacturer;

    private String strength;

    private String dosageForm;

    private String activeSubstance;

    private Boolean isAvailable;

}
