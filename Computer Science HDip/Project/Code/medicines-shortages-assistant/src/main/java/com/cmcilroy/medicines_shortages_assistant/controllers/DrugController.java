package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.cmcilroy.medicines_shortages_assistant.paginators.Paginator;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;

@RestController
public class DrugController {

    // inject DrugService, Mapper and Paginator
    private DrugService drugService;

    private Mapper<DrugEntity, DrugDto> drugMapper;

    private Paginator<DrugDto> paginator;

    public DrugController(DrugService drugService, Mapper<DrugEntity, DrugDto> drugMapper, Paginator<DrugDto> paginator) {
        this.drugService = drugService;
        this.drugMapper = drugMapper;
        this.paginator = paginator;
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
            // instantiate List to hold results
            List<DrugEntity> drugs = new ArrayList<>();
            // if active substance contains commas or forward slashes (i.e. it is a combination drug)
            if(activeSubstance.contains(",")){
                // find all drugs in the database with the same combination of active substances
                drugs.addAll(drugService.findAllByComboActiveSubstances(activeSubstance));
            }
            else{
                // find all drugs in the database with the same active substance
                drugs.addAll(drugService.findAllByActiveSubstance(activeSubstance));
            }
            // if the list is empty, return a HTTP 404 Not Found
            if(drugs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // otherwise map the list of DrugEntities to a list of DrugDtos
            List<DrugDto> drugDtos = StreamSupport.stream(drugs.spliterator(), false)
                    .map(drugMapper::mapTo)
                    .collect(Collectors.toList());

            // return paginated list
            return new ResponseEntity<>(paginator.paginate(drugDtos, pageable), HttpStatus.OK);

    }

    // GET method to return a paginated list of all alternative product names contained in the database 
    // with the same active substance(s) as the product name passed in
    @GetMapping(path = "/drugs/search-by-product-name")
    public ResponseEntity<Page<DrugDto>> listAlternativeDrugNames(@RequestParam String productName, Pageable pageable) {
        // find all DrugEntity records with product name passed in
        List<DrugEntity> drugs = drugService.findByContainsProductName(productName);
        // replace the word 'and' with '&' and repeat search
        drugs.addAll(drugService.findByContainsProductName(productName.replace("and", "&")));
        // replace the word '&' with 'and' and repeat search
        drugs.addAll(drugService.findByContainsProductName(productName.replace("&", "and")));
        // if the list is empty, then no matching records were found
        if(drugs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            // instantiate Set to hold alternative products (using a set prevents duplicates)
            Set<DrugEntity> altProducts = new HashSet<>();
            // for each DrugEntity in the list
            for(DrugEntity drug : drugs) {
                // find active substance(s) associated with that DrugEntity
                String activeSubstance = drug.getActiveSubstance();
                // if active substance contains commas or forward slashes (i.e. it is a combination drug)
                if(activeSubstance.contains(",")){
                    // find all drugs in the database with the same combination of active substances
                    List<DrugEntity> drugsSameActives = drugService.findAllByComboActiveSubstances(activeSubstance);
                    // add them to the set
                    altProducts.addAll(drugsSameActives);
                }
                // otherwise if the drug is not a combination drug (i.e. single active substance)
                else{
                    // find all drugs in the database with the same active substance and add these to the set
                    List<DrugEntity> drugsSameActive = drugService.findAllByActiveSubstance(activeSubstance);
                    altProducts.addAll(drugsSameActive);
                }
            }
            // if the altProducts list is empty
            if(altProducts.isEmpty()) {
                // no alternative products found containing the same active substance(s), return HTTP status 404 Not Found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // otherwise, convert the altProducts list to a Page of DrugDtos and return this with a HTTP status 200 OK
            else {
                // map altProducts to list of DrugDtos
                List<DrugDto> dtos = StreamSupport.stream(altProducts.spliterator(), false)
                                    .map(drugMapper::mapTo)
                                    .collect(Collectors.toList());

                return new ResponseEntity<>(paginator.paginate(dtos, pageable), HttpStatus.OK);
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
