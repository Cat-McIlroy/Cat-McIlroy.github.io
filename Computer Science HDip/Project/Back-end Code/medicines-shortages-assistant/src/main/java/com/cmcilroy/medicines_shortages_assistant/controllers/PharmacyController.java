package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // using PUT rather than POST because I am providing the Id (psiRegNo)
    @PutMapping(path = "/pharmacies/{psiRegNo}")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the PharmacyDto object represented as JSON and convert to Java
    public ResponseEntity<PharmacyDto> createUpdatePharmacy(
    @PathVariable("psiRegNo") Integer psiRegNo,
    @RequestBody PharmacyDto pharmacyDto) {
        // map Dto to Entity
        PharmacyEntity pharmacyEntity = pharmacyMapper.mapFrom(pharmacyDto);
        // check if pharmacy already exists in the database
        boolean pharmacyExists = pharmacyService.isPresent(psiRegNo);
        // save the Entity in the database
        PharmacyEntity savedPharmacyEntity = pharmacyService.savePharmacy(psiRegNo, pharmacyEntity);
        // map the Entity back to Dto
        PharmacyDto savedPharmacyDto = pharmacyMapper.mapTo(savedPharmacyEntity);
        // return the relevant HTTP status code
        if(pharmacyExists){
            // update existing record
            return new ResponseEntity<>(savedPharmacyDto, HttpStatus.OK);
        }
        else{
            // create new record
            return new ResponseEntity<>(savedPharmacyDto, HttpStatus.CREATED);
        }
    }

    // GET method to return list of all pharmacies contained in the database
    @GetMapping(path = "/pharmacies")
    public List<PharmacyDto> listAllPharmacies() {
        List<PharmacyEntity> pharmacies = pharmacyService.findAll();
        return pharmacies.stream()
        .map(pharmacyMapper::mapTo)
        .collect(Collectors.toList());
    }

    // GET method to return pharmacy by psiRegNo
    @GetMapping(path = "/pharmacies/{psiRegNo}")
    public ResponseEntity<PharmacyDto> getPharmacy(@PathVariable("psiRegNo") Integer psiRegNo) {
        Optional<PharmacyEntity> foundPharmacy = pharmacyService.findOne(psiRegNo);
        if(foundPharmacy.isPresent()){
            PharmacyEntity pharmacyEntity = foundPharmacy.get();
            PharmacyDto pharmacyDto = pharmacyMapper.mapTo(pharmacyEntity);
            return new ResponseEntity<>(pharmacyDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
