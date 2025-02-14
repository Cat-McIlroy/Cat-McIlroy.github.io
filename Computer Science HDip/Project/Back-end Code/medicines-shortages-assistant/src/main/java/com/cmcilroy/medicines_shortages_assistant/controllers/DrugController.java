package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.mappers.Mapper;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;

@RestController
public class DrugController {

    // inject DrugService and Mapper
    private DrugService drugService;

    private Mapper<DrugEntity, DrugDto> drugMapper;

    public DrugController(DrugService drugService, Mapper<DrugEntity, DrugDto> drugMapper) {
        this.drugService = drugService;
        this.drugMapper = drugMapper;
    }

    // using PUT rather than POST because I am providing the Id (licenceNo)
    @PutMapping(path = "/drugs/{licenceNo}")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the DrugDto object represented as JSON and convert to Java
    public ResponseEntity<DrugDto> createUpdateDrug(
    @PathVariable("licenceNo") String licenceNo,
    @RequestBody DrugDto drugDto) {
        // decode licence no from URL format
        String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
        // map Dto to Entity
        DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
        // check if drug already exists in the database
        boolean drugExists = drugService.isPresent(decodedLicenceNo);
        // save the Entity in the database
        DrugEntity savedDrugEntity = drugService.saveDrug(decodedLicenceNo, drugEntity);
        // map the entity back to a Dto
        DrugDto savedDrugDto = drugMapper.mapTo(savedDrugEntity);
        // return the relevant HTTP status code
        if(drugExists){
            // update existing record
            return new ResponseEntity<>(savedDrugDto, HttpStatus.OK);
        }
        else{
            // create new record
            return new ResponseEntity<>(savedDrugDto, HttpStatus.CREATED);
        }
    }

    // GET method to return list of all drugs contained in the database
    @GetMapping(path = "/drugs")
    public List<DrugDto> listAllDrugs() {
        List<DrugEntity> drugs = drugService.findAll();
        return drugs.stream()
        .map(drugMapper::mapTo)
        .collect(Collectors.toList());
    }

    // GET method to return drug by licenceNo
    @GetMapping(path = "/drugs/{licenceNo}")
    public ResponseEntity<DrugDto> getDrug(@PathVariable("licenceNo") String licenceNo) {
        String decodedLicenceNo = URLDecoder.decode(licenceNo, StandardCharsets.UTF_8);
        Optional<DrugEntity> foundDrug = drugService.findOne(decodedLicenceNo);
        if(foundDrug.isPresent()){
            DrugEntity drugEntity = foundDrug.get();
            DrugDto drugDto = drugMapper.mapTo(drugEntity);
            return new ResponseEntity<>(drugDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
