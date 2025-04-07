package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.List;
import java.util.Optional;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

public interface DrugService {
    
    Iterable<DrugEntity> findAllByIsAvailable(boolean isAvailable);

    List<DrugEntity> findAllByActiveSubstance(String activeSubstance);

    List<DrugEntity> findAllByComboActiveSubstances(String activeSubstance);

    List<DrugEntity> findByContainsProductName(String productName);

    Iterable<DrugEntity> findAll();

    Optional<DrugEntity> findOne(String licenceNo);

}