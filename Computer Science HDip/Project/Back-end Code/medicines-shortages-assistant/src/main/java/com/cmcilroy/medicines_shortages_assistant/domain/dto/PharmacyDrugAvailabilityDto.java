package com.cmcilroy.medicines_shortages_assistant.domain.dto;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;

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

    private PharmacyEntity pharmacy;

    private DrugEntity drug;

    private Boolean isAvailable;

}
