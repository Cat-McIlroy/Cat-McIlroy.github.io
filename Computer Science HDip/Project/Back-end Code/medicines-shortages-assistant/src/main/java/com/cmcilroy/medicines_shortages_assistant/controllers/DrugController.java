package com.cmcilroy.medicines_shortages_assistant.controllers;

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

    // using PUT rather than POST because I am providing the Id
    @PutMapping(path = "/drugs")
    // RequestBodyAnnotation tells Spring to look at the HTTP request body for the DrugDto object represented as JSON and convert to Java
    public DrugDto createDrug(@RequestBody DrugDto drugDto) {
        DrugEntity drugEntity = drugMapper.mapFrom(drugDto);
        DrugEntity savedDrugEntity = drugService.createDrug(drugEntity);
        return drugMapper.mapTo(savedDrugEntity);
    }

}
