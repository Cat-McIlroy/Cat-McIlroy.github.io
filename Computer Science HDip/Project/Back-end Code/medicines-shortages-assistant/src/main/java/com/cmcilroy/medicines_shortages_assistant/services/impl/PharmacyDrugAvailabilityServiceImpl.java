package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public PharmacyDrugAvailabilityEntity savePharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability) {
        // save returns an Entity by default
        return pharmacyDrugAvailabilityRepository.save(pharmacyDrugAvailability);
    }

    @Override
    public List<PharmacyDrugAvailabilityEntity> findAll() {
        // convert the Iterable by using spliterator on the Iterable returned by findAll and collecting the stream as a List
        return StreamSupport.stream(pharmacyDrugAvailabilityRepository.
        findAll().
        spliterator(), 
        false).
        collect(Collectors.toList());
    }

    @Override
    public Optional<PharmacyDrugAvailabilityEntity> findOne(Long id) {
        return pharmacyDrugAvailabilityRepository.findById(id);
    }

    @Override
    public boolean isPresent(Long id) {
        return pharmacyDrugAvailabilityRepository.existsById(id);
    }

}
