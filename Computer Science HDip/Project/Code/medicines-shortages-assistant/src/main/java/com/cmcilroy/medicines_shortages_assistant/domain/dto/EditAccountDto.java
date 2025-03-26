package com.cmcilroy.medicines_shortages_assistant.domain.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditAccountDto {
    
    private PharmacyDto pharmacy;

    private String currentPassword;

}
