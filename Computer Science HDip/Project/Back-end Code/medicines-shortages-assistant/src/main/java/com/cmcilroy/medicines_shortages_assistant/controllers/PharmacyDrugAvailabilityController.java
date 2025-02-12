package com.cmcilroy.medicines_shortages_assistant.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDrugAvailabilityDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;

@RestController
public class PharmacyDrugAvailabilityController {

    // inject PharmacyDrugAvailabilityService and Mapper
    private PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService;

    private Mapper<PharmacyDrugAvailabilityEntity, PharmacyDrugAvailabilityDto> pharmacyDrugAvailabilityMapper;

    public PharmacyDrugAvailabilityController(PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService, Mapper<PharmacyDrugAvailabilityEntity, PharmacyDrugAvailabilityDto> pharmacyDrugAvailabilityMapper) {
        this.pharmacyDrugAvailabilityService = pharmacyDrugAvailabilityService;
        this.pharmacyDrugAvailabilityMapper = pharmacyDrugAvailabilityMapper;
    }

    // using POST rather than PUT because I am not providing the Id
    @PostMapping(path = "/pharmacy-drug-availabilities")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the PharmacyDrugAvailabilityDto object represented as JSON and convert to Java
    public PharmacyDrugAvailabilityDto createPharmacyDrugAvailability(@RequestBody PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto) {
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = pharmacyDrugAvailabilityMapper.mapFrom(pharmacyDrugAvailabilityDto);
        PharmacyDrugAvailabilityEntity savedPharmacyDrugAvailabilityEntity = pharmacyDrugAvailabilityService.createPharmacyDrugAvailability(pharmacyDrugAvailabilityEntity);
        return pharmacyDrugAvailabilityMapper.mapTo(savedPharmacyDrugAvailabilityEntity);
    }
}
