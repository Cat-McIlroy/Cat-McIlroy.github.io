package com.cmcilroy.medicines_shortages_assistant.services.impl;

import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;

@Service 
public class PharmacyDrugAvailabilityServiceImpl implements PharmacyDrugAvailabilityService{

    // inject Repository
    private PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository;

    public PharmacyDrugAvailabilityServiceImpl(PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository) {
        this.pharmacyDrugAvailabilityRepository = pharmacyDrugAvailabilityRepository;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public PharmacyDrugAvailabilityEntity createPharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability) {
        // save returns an Entity by default
        return pharmacyDrugAvailabilityRepository.save(pharmacyDrugAvailability);
    }



}
