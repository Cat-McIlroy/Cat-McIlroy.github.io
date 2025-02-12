package com.cmcilroy.medicines_shortages_assistant.services.impl;

import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;

@Service
public class PharmacyServiceImpl implements PharmacyService{

    // inject Repository
    private PharmacyRepository pharmacyRepository;

    public PharmacyServiceImpl(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public PharmacyEntity createPharmacy(PharmacyEntity pharmacy) {
        // save returns an Entity by default
        return pharmacyRepository.save(pharmacy);
    }

}
