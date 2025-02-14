package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.List;
import java.util.Optional;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

public interface DrugService {

    DrugEntity saveDrug(String licenceNo, DrugEntity drug);
    
    List<DrugEntity> findAll();
    
    Optional<DrugEntity> findOne(String licenceNo);

    boolean isPresent(String licenceNo);

}