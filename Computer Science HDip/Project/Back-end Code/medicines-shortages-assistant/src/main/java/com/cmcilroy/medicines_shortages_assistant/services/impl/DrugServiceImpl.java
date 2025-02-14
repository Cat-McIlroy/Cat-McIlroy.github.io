package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public DrugEntity saveDrug(String licenceNo, DrugEntity drug) {
        // ensure the licenceNo associated with the drug object to be saved is the same as the licenceNo in the URL
        drug.setLicenceNo(licenceNo);
        // save returns an Entity by default
        return drugRepository.save(drug);
    }

    @Override
    public List<DrugEntity> findAll() {
        return StreamSupport
        .stream(
            drugRepository.findAll().spliterator(), 
            false)
        .collect(Collectors.toList());
    }

    @Override
    public Optional<DrugEntity> findOne(String licenceNo) {
        return drugRepository.findById(licenceNo);
    }

    @Override
    public boolean isPresent(String licenceNo) {
        return drugRepository.existsById(licenceNo);
    }

    
}
