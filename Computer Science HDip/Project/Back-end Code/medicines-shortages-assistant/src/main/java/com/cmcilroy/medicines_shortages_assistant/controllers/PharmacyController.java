package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    // PATCH method to partially update a pharmacy contained in the database
    @PatchMapping(path = "/pharmacies/{psiRegNo}")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the DrugDto object represented as JSON and convert to Java
    public ResponseEntity<PharmacyDto> partialUpdatePharmacy(
    @PathVariable("psiRegNo") Integer psiRegNo,
    @RequestBody PharmacyDto pharmacyDto) {
        // check if pharmacy exists in the database
        if(!pharmacyService.isPresent(psiRegNo)){
            // if it doesn't exist return HTTP 404 Not Found, cannot partially update a non-existent record
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            // map Dto to Entity
            PharmacyEntity pharmacyEntity = pharmacyMapper.mapFrom(pharmacyDto);
            // save the Entity in the database
            PharmacyEntity updatedPharmacyEntity = pharmacyService.partialUpdate(psiRegNo, pharmacyEntity);
            // map the entity back to a Dto
            PharmacyDto updatedPharmacyDto = pharmacyMapper.mapTo(updatedPharmacyEntity);
            return new ResponseEntity<>(updatedPharmacyDto, HttpStatus.OK);
        }
    }

    // GET method to return a paginated list of all pharmacies contained in the database
    @GetMapping(path = "/pharmacies")
    public Page<PharmacyDto> listAllPharmacies(Pageable pageable) {
        Page<PharmacyEntity> pharmacies = pharmacyService.findAll(pageable);
        return pharmacies.map(pharmacyMapper::mapTo);
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

    // DELETE method to delete a specific pharmacy from the database
    @DeleteMapping(path = "/pharmacies/{psiRegNo}")
    public ResponseEntity<Void> deletePharmacy(@PathVariable("psiRegNo") Integer psiRegNo) {
        pharmacyService.delete(psiRegNo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
