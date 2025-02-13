package com.cmcilroy.medicines_shortages_assistant.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // required for Jackson, typically creates object using NoArgsConstructor then uses Setters to set values on the object
@Builder
public class PharmacyDrugAvailabilityDto {

    private Long id;

    private PharmacyDto pharmacy;

    private DrugDto drug;

    private Boolean isAvailable;

}
