package com.cmcilroy.medicines_shortages_assistant.cleaner.impl;

// clears the database 

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;

@Component
public class DatabaseCleanerImpl implements DatabaseCleaner { 

    private PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository;
    private DrugRepository drugRepository;
    private PharmacyRepository pharmacyRepository;

    public DatabaseCleanerImpl(
        PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository, 
        DrugRepository drugRepository,
        PharmacyRepository pharmacyRepository
    ) {
        this.pharmacyDrugAvailabilityRepository = pharmacyDrugAvailabilityRepository;
        this.drugRepository = drugRepository;
        this.pharmacyRepository = pharmacyRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearDatabase() {
        pharmacyDrugAvailabilityRepository.deleteAll();
        drugRepository.deleteAll();
        pharmacyRepository.deleteAll();
    }

}
