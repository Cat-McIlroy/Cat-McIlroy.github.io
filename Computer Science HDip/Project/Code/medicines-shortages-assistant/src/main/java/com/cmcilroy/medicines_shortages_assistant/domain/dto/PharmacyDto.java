package com.cmcilroy.medicines_shortages_assistant.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
@Builder
public class PharmacyDto {
    
    private Integer psiRegNo;

    private String pharmacyName;

    private String address;

    private String eircode;

    private String phoneNo;

    private String email;

    private String password;

}
