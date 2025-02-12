package com.cmcilroy.medicines_shortages_assistant.services;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

public interface DrugService {

    DrugEntity createDrug(DrugEntity drug);
    
}