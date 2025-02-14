package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // POST method to create a new pharmacy drug availability in the database
    // using POST rather than PUT because I am not providing the Id
    @PostMapping(path = "/pharmacy-drug-availabilities")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the PharmacyDrugAvailabilityDto object represented as JSON and convert to Java
    public ResponseEntity<PharmacyDrugAvailabilityDto> createPharmacyDrugAvailability(@RequestBody PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto) {
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = pharmacyDrugAvailabilityMapper.mapFrom(pharmacyDrugAvailabilityDto);
        PharmacyDrugAvailabilityEntity savedPharmacyDrugAvailabilityEntity = pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailabilityEntity);
        return new ResponseEntity<>(pharmacyDrugAvailabilityMapper.mapTo(savedPharmacyDrugAvailabilityEntity), HttpStatus.CREATED);
    }

    // GET method to return a list of all pharmacy drug availabilities in the database
    @GetMapping(path = "/pharmacy-drug-availabilities")
    public List<PharmacyDrugAvailabilityDto> listAllPharmacyDrugAvailabilities() {
        List<PharmacyDrugAvailabilityEntity> availabilities = pharmacyDrugAvailabilityService.findAll();
        return availabilities.stream()
        .map(pharmacyDrugAvailabilityMapper::mapTo)
        .collect(Collectors.toList());
    }

    // GET method to return one pharmacy drug availability by Id
    @GetMapping(path = "/pharmacy-drug-availabilities/{id}")
    public ResponseEntity<PharmacyDrugAvailabilityDto> getPharmacyDrugAvailability(@PathVariable("id") Long id) {
        Optional<PharmacyDrugAvailabilityEntity> foundAvailability = pharmacyDrugAvailabilityService.findOne(id);
        if(foundAvailability.isPresent()){
            PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = foundAvailability.get();
            PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto = pharmacyDrugAvailabilityMapper.mapTo(pharmacyDrugAvailabilityEntity);
            return new ResponseEntity<>(pharmacyDrugAvailabilityDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT method to fully update a specific pharmacy drug availability
    @PutMapping(path = "/pharmacy-drug-availabilities/{id}")
    public ResponseEntity<PharmacyDrugAvailabilityDto> fullUpdatePharmacyDrugAvailability(
        @PathVariable("id") Long id,
        @RequestBody PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto
    ) {
        // if the specified pharmacy drug availability does not exist in the database
        if(!pharmacyDrugAvailabilityService.isPresent(id)){
            // return a HTTP 404 Not Found 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // set id of pharmacyDrugAvailabilityDto to be id in URL path, to ensure consistency
        pharmacyDrugAvailabilityDto.setId(id);
        // map to entity
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = 
        pharmacyDrugAvailabilityMapper.mapFrom(pharmacyDrugAvailabilityDto);
        // save entity to database
        PharmacyDrugAvailabilityEntity savedPharmacyDrugAvailabilityEntity = 
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailabilityEntity);
        // map the saved entity back to a dto and return this object inside a ResponseEntity with a HTTP 200 Ok 
        return new ResponseEntity<>(pharmacyDrugAvailabilityMapper.mapTo(savedPharmacyDrugAvailabilityEntity), HttpStatus.OK);

    }
}
