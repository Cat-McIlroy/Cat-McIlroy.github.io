package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDrugAvailabilityDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.paginators.Paginator;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;

@RestController
public class PharmacyDrugAvailabilityController {

    // inject PharmacyDrugAvailabilityService, DrugService, Mappers and Paginator
    private PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService;

    private DrugService drugService;

    private Mapper<PharmacyDrugAvailabilityEntity, PharmacyDrugAvailabilityDto> pharmacyDrugAvailabilityMapper;
    private Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper;

    private Paginator<PharmacyDrugAvailabilityDto> paginator;

    // constructor injection
    public PharmacyDrugAvailabilityController(
        PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService, 
        Mapper<PharmacyDrugAvailabilityEntity, 
        PharmacyDrugAvailabilityDto> pharmacyDrugAvailabilityMapper,
        Mapper<PharmacyEntity, PharmacyDto> pharmacyMapper,
        DrugService drugService,
        Paginator<PharmacyDrugAvailabilityDto> paginator
        ) {
        this.pharmacyDrugAvailabilityService = pharmacyDrugAvailabilityService;
        this.pharmacyDrugAvailabilityMapper = pharmacyDrugAvailabilityMapper;
        this.pharmacyMapper = pharmacyMapper;
        this.drugService = drugService;
        this.paginator = paginator;
    }

    //////////////////////////////////////////////////////// ALL USERS //////////////////////////////////////////////////////////////

    /////////////////////////////////// SEARCH FOR PHARMACY STOCK AVAILABILITY OF A PRODUCT /////////////////////////////////////////

    // GET method to return a paginated list of pharmacy drug availabilities by licence no
    @GetMapping(path = "/pharmacy-drug-availabilities/search-for-stock")
    public ResponseEntity<Page<PharmacyDrugAvailabilityDto>> listPharmacyDrugAvailabilitiesByDrug(
        @RequestParam String licenceNo, 
        Pageable pageable
    ) {
        String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
        Page<PharmacyDrugAvailabilityEntity> availabilities = pharmacyDrugAvailabilityService.findAllByLicenceNo(decodedLicenceNo, pageable);
        if(availabilities.hasContent()) {
            return new ResponseEntity<>(availabilities.map(pharmacyDrugAvailabilityMapper::mapTo), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //////////////////////////////////////////////// PHARMACY USERS ONLY ////////////////////////////////////////////////////////////

    ////////////////////////////////////////// VIEW EXISTING STOCK AVAILABILITY LISTINGS ////////////////////////////////////////

    // GET method to return a paginated list of all pharmacy drug availabilities associated with the current pharmacy user
    @GetMapping(path = "/pharmacy-drug-availabilities/view-all")
    public ResponseEntity<Page<PharmacyDrugAvailabilityDto>> listAllPharmacyDrugAvailabilities(Pageable pageable) {
        // get the pharmacy dto object from the pharmacy user currently signed in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PharmacyDto pharmacyDto = (PharmacyDto) authentication.getPrincipal();
        // map the pharmacy dto to a pharmacy entity
        PharmacyEntity pharmacy = pharmacyMapper.mapFrom(pharmacyDto);
        // retrieve all pharmacy drug availability listings associated with the current authenticated user and return as a list
        List<PharmacyDrugAvailabilityEntity> availabilities = pharmacyDrugAvailabilityService.findAllByPharmacy(pharmacy);
        // if list is empty, return HTTP 204 No Content
        if (availabilities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // otherwise map entities to dto objects and return with HTTP 200 OK
        List<PharmacyDrugAvailabilityDto> dtos = StreamSupport.stream(availabilities.spliterator(), false)
                                    .map(pharmacyDrugAvailabilityMapper::mapTo)
                                    .collect(Collectors.toList());

        return new ResponseEntity<>(paginator.paginate(dtos, pageable), HttpStatus.OK);
    }

    ///////////////////////////////////////// CREATE A NEW STOCK AVAILABILITY LISTING ////////////////////////////////////////////////

    // POST method to create a new pharmacy drug availability in the database
    @PostMapping(path = "/pharmacy-drug-availabilities/create")
    public ResponseEntity<PharmacyDrugAvailabilityDto> createPharmacyDrugAvailability(
        @RequestParam String licenceNo,
        @RequestParam boolean isAvailable
    ) {
        // get the pharmacy dto object from the pharmacy user currently signed in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PharmacyDto pharmacyDto = (PharmacyDto) authentication.getPrincipal();
        // map the pharmacy dto to a pharmacy entity
        PharmacyEntity pharmacy = pharmacyMapper.mapFrom(pharmacyDto);
        // get the drug object from the licence number of the product selected
        String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
        DrugEntity drug = drugService.findOne(decodedLicenceNo).get();
        // check if this pharmacy has an existing pharmacy drug availability record for this drug object 
        if(pharmacyDrugAvailabilityService.existsByPharmacyAndDrug(pharmacy, drug)) {
            // if record already exists, return Http Conflict
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        // build pharmacy drug availability object
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = 
            PharmacyDrugAvailabilityEntity.builder()
                        // id is automatically generated
                        .pharmacy(pharmacy)
                        .drug(drug)
                        .isAvailable(isAvailable)
                        .build();
        // persist the newly created pharmacy drug availability in the database
        PharmacyDrugAvailabilityEntity savedPharmacyDrugAvailabilityEntity = 
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailabilityEntity);
        // return newly created pharmacy drug availability as a dto, and HTTP 201 Created
        return new ResponseEntity<>(pharmacyDrugAvailabilityMapper.mapTo(savedPharmacyDrugAvailabilityEntity), HttpStatus.CREATED);
    }

    ///////////////////////////////////////// DELETE AN EXISTING STOCK AVAILABILITY LISTING ///////////////////////////////////////////////

    // DELETE method to delete a specific pharmacy drug availability from the database
    @DeleteMapping(path = "/pharmacy-drug-availabilities/delete/{id}")
    public ResponseEntity<Void> deletePharmacyDrugAvailability(@PathVariable("id") Long id){
        // delete is a void method so doesn't return anything
        pharmacyDrugAvailabilityService.delete(id);
        // return HTTP 204 No Content
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
