package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public PharmacyEntity savePharmacy(Integer psiRegNo, PharmacyEntity pharmacy) {
        // ensure the psiRegNo associated with the pharmacy object to be saved is the same as the psiRegNo in the URL
        pharmacy.setPsiRegNo(psiRegNo);
        // save returns an Entity by default
        return pharmacyRepository.save(pharmacy);
    }

    @Override
    public List<PharmacyEntity> findAll() {
        return StreamSupport
        .stream(
            pharmacyRepository.findAll().spliterator(), 
            false)
        .collect(Collectors.toList());
    }

    @Override
    public Optional<PharmacyEntity> findOne(Integer psiRegNo) {
        return pharmacyRepository.findById(psiRegNo);
    }

    @Override
    public boolean isPresent(Integer psiRegNo) {
        return pharmacyRepository.existsById(psiRegNo);
    }

}
