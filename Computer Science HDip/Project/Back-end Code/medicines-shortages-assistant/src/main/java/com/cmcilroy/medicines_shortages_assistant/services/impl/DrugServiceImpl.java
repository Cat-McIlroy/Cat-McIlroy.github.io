package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<DrugEntity> findAll(Pageable pageable) {
        return drugRepository.findAll(pageable);
    }

    @Override
    public Optional<DrugEntity> findOne(String licenceNo) {
        return drugRepository.findById(licenceNo);
    }

    @Override
    public boolean isPresent(String licenceNo) {
        return drugRepository.existsById(licenceNo);
    }

    @Override
    public DrugEntity partialUpdate(String licenceNo, DrugEntity drug) {
        // make sure licenceNo of the drug entity passed in is the same as the licenceNo in the URL
        drug.setLicenceNo(licenceNo);
        // retrieve record from the database
        return drugRepository.findById(licenceNo).map(existingRecord -> {
            // update product name (useful in the case of a re-brand)
            Optional.ofNullable(drug.getProductName()).ifPresent(existingRecord::setProductName);
            // update product availability 
            Optional.ofNullable(drug.getIsAvailable()).ifPresent(existingRecord::setIsAvailable);
            // do not support partial update of licence number, active substance or strength, as these should stay constant
            // if the active substance or strength changed, this would be a different product with a different licence number
            return drugRepository.save(existingRecord);
        }).orElseThrow(() -> new RuntimeException("Record does not exist."));
    }

    @Override
    public void delete(String licenceNo) {
        drugRepository.deleteById(licenceNo);
    }
    
}
