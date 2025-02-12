package com.cmcilroy.medicines_shortages_assistant.services.impl;

import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;

@Service
public class DrugServiceImpl implements DrugService{

   // inject Repository
    private DrugRepository drugRepository;

    public DrugServiceImpl(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public DrugEntity createDrug(DrugEntity drug) {
        // save returns an Entity by default
        return drugRepository.save(drug);
    }

}
