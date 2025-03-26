package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;

@RestController
public class DrugController {

    // inject DrugService and Mapper and Scraper
    private DrugService drugService;

    private Mapper<DrugEntity, DrugDto> drugMapper;

    public DrugController(DrugService drugService, Mapper<DrugEntity, DrugDto> drugMapper) {
        this.drugService = drugService;
        this.drugMapper = drugMapper;
    }

    // // using PUT rather than POST because I am providing the Id (licenceNo)
    // @PutMapping(path = "/drugs/{licenceNo}")
    // // RequestBodyAnnotation tells Spring to look at the HTTP request body for the DrugDto object represented as JSON and convert to Java
    // public ResponseEntity<DrugDto> createUpdateDrug(
    // @PathVariable("licenceNo") String licenceNo,
    // @RequestBody DrugDto drugDto) {
    //     // decode licence no from URL format
    //     String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
    //     // map Dto to Entity
    //     DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
    //     // check if drug already exists in the database
    //     boolean drugExists = drugService.isPresent(decodedLicenceNo);
    //     // save the Entity in the database
    //     DrugEntity savedDrugEntity = drugService.saveDrug(decodedLicenceNo, drugEntity);
    //     // map the entity back to a Dto
    //     DrugDto savedDrugDto = drugMapper.mapTo(savedDrugEntity);
    //     // return the relevant HTTP status code
    //     if(drugExists){
    //         // update existing record
    //         return new ResponseEntity<>(savedDrugDto, HttpStatus.OK);
    //     }
    //     else{
    //         // create new record
    //         return new ResponseEntity<>(savedDrugDto, HttpStatus.CREATED);
    //     }
    // }

    // // PATCH method to partially update a drug contained in the database
    // @PatchMapping(path = "/drugs/{licenceNo}")
    // // RequestBodyAnnotation tells Spring to look at the HTTP request body for the DrugDto object represented as JSON and convert to Java
    // public ResponseEntity<DrugDto> partialUpdateDrug(
    // @PathVariable("licenceNo") String licenceNo,
    // @RequestBody DrugDto drugDto) {
    //     // decode licence no from URL format
    //     String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);

    //     // check if drug exists in the database
    //     if(!drugService.isPresent(decodedLicenceNo)){
    //         // if it doesn't exist return HTTP 404 Not Found, cannot partially update a non-existent record
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    //     else{
    //         // map Dto to Entity
    //         DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
    //         // save the Entity in the database
    //         DrugEntity updatedDrugEntity = drugService.partialUpdate(decodedLicenceNo, drugEntity);
    //         // map the entity back to a Dto
    //         DrugDto updatedDrugDto = drugMapper.mapTo(updatedDrugEntity);
    //         return new ResponseEntity<>(updatedDrugDto, HttpStatus.OK);
    //     }
    // }


    // GET method to return a paginated list of all drugs contained in the database
    @GetMapping(path = "/drugs")
    public Page<DrugDto> listAllDrugs(Pageable pageable) {
        Page<DrugEntity> drugs = drugService.findAll(pageable);
        return drugs.map(drugMapper::mapTo);
    }

    // GET method to return a list of all drugs currently in short supply
    @GetMapping(path = "/drugs/shortages")
    public List<DrugDto> listAllShorts() {

        Iterable<DrugEntity> shorts = drugService.findAllByIsAvailable(false);

        // map the iterable to a list of dtos
        List<DrugDto> shortsDtos = StreamSupport.stream(shorts.spliterator(), false)
        .map(drugMapper::mapTo)
        .collect(Collectors.toList());

        return shortsDtos;

    }

    // GET method to return a paginated list of all products contained in the database 
    // with the active substance passed in
    @GetMapping(path = "/drugs/search-by-active-substance")
    public ResponseEntity<Page<DrugDto>> listDrugNamesByActiveSubstance(@RequestParam String activeSubstance, Pageable pageable) {
            // if active substance contains commas or forward slashes (i.e. it is a combination drug)
            if(activeSubstance.contains(",") || activeSubstance.contains("/")){
                // replace forward slash with comma, as active substance combinations in the database are comma separated
                activeSubstance = activeSubstance.replace('/', ',');
                // find all drugs in the database with the same combination of active substances
                Page<DrugEntity> drugs = drugService.findAllByComboActiveSubstances(activeSubstance, pageable);
                if(drugs.hasContent()) {
                    return new ResponseEntity<>(drugs.map(drugMapper::mapTo), HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
            else{
                // find all drugs in the database with the same active substance and return these as a Paginated list
                Page<DrugEntity> drugs = drugService.findAllByActiveSubstance(activeSubstance, pageable);
                if(drugs.hasContent()) {
                    return new ResponseEntity<>(drugs.map(drugMapper::mapTo), HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
    }

    // GET method to return a paginated list of all alternative product names contained in the database 
    // with the same active substance as the product name passed in
    @GetMapping(path = "/drugs/search-by-product-name")
    public ResponseEntity<Page<DrugDto>> listAlternativeDrugNames(@RequestParam String productName, Pageable pageable) {
        // find DrugEntity with product name passed in
        Optional<DrugEntity> drug = drugService.findByContainsProductName(productName);
        if(drug.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            // find active substance associated with product name
            String activeSubstance = drug.get().getActiveSubstance();
            // if active substance doesn't contain commas (i.e. it is a single active substance not a combination drug)
            if(activeSubstance.contains(",")){
                // find all drugs in the database with the same combination of active substances
                Page<DrugEntity> drugs = drugService.findAllByComboActiveSubstances(activeSubstance, pageable);
                if(drugs.hasContent()){
                    return new ResponseEntity<>(drugs.map(drugMapper::mapTo), HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
            else{
                // find all drugs in the database with the same active substance and return these as a Paginated list
                Page<DrugEntity> drugs = drugService.findAllByActiveSubstance(activeSubstance, pageable);
                if(drugs.hasContent()){
                    return new ResponseEntity<>(drugs.map(drugMapper::mapTo), HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
        }
    }

    // // GET method to return drug by licenceNo
    // @GetMapping(path = "/drugs/{licenceNo}")
    // public ResponseEntity<DrugDto> getDrug(@PathVariable("licenceNo") String licenceNo) {
    //     String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
    //     Optional<DrugEntity> foundDrug = drugService.findOne(decodedLicenceNo);
    //     if(foundDrug.isPresent()){
    //         DrugEntity drugEntity = foundDrug.get();
    //         DrugDto drugDto = drugMapper.mapTo(drugEntity);
    //         return new ResponseEntity<>(drugDto, HttpStatus.OK);
    //     }
    //     else{
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    // }

    // // DELETE method to delete a specific drug from the database
    // @DeleteMapping(path = "/drugs/{licenceNo}")
    // public ResponseEntity<Void> deleteDrug(@PathVariable("licenceNo") String licenceNo) {
    //     String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
    //     drugService.delete(decodedLicenceNo);
    //     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }

}
