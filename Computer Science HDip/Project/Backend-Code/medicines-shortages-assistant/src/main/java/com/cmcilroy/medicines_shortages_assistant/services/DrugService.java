package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

public interface DrugService {

    DrugEntity saveDrug(String licenceNo, DrugEntity drug);
    
    // findAll method which allows for pagination
    Page<DrugEntity> findAll(Pageable pageable);

    // non paginated findAll
    Iterable<DrugEntity> findAll();

    Iterable<DrugEntity> findAllByIsAvailable(boolean isAvailable);

    Page<DrugEntity> findAllByActiveSubstance(String activeSubstance, Pageable pageable);

    Page<DrugEntity> findAllByComboActiveSubstances(String activeSubstance, Pageable pageable);

    Optional<DrugEntity> findByContainsProductName(String productName);

    Optional<DrugEntity> findByExactProductName(String productName);
    
    Optional<DrugEntity> findOne(String licenceNo);

    boolean isPresent(String licenceNo);

    DrugEntity partialUpdate(String licenceNo, DrugEntity drug);

    void delete(String licenceNo);

}