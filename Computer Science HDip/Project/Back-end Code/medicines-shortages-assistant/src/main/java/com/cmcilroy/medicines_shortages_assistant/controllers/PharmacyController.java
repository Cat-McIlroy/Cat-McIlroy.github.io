package com.cmcilroy.medicines_shortages_assistant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;

@RestController
public class PharmacyController {

    // inject PharmacyService and Mapper
    private PharmacyService pharmacyService;

    private Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper;

    public PharmacyController(PharmacyService pharmacyService, Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper) {
        this.pharmacyService = pharmacyService;
        this.pharmacyMapper = pharmacyMapper;
    }

    // using PUT rather than POST because I am providing the Id
    @PutMapping(path = "/pharmacies")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the PharmacyDto object represented as JSON and convert to Java
    public ResponseEntity<PharmacyDto> createPharmacy(@RequestBody PharmacyDto pharmacyDto) {
        PharmacyEntity pharmacyEntity = pharmacyMapper.mapFrom(pharmacyDto);
        PharmacyEntity savedPharmacyEntity = pharmacyService.createPharmacy(pharmacyEntity);
        return new ResponseEntity<>(pharmacyMapper.mapTo(savedPharmacyEntity), HttpStatus.CREATED);
    }
}
