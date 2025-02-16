package com.cmcilroy.medicines_shortages_assistant.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;

public interface DrugService {

    DrugEntity saveDrug(String licenceNo, DrugEntity drug);
    
    // findAll method which allows for pagination
    Page<DrugEntity> findAll(Pageable pageable);
    
    Optional<DrugEntity> findOne(String licenceNo);

    boolean isPresent(String licenceNo);

    DrugEntity partialUpdate(String licenceNo, DrugEntity drug);

    void delete(String licenceNo);

}