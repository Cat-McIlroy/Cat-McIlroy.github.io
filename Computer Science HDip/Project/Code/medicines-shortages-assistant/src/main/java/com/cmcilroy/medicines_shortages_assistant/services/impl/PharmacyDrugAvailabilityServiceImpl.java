package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;

import jakarta.transaction.Transactional;

@Service 
public class PharmacyDrugAvailabilityServiceImpl implements PharmacyDrugAvailabilityService{

    // inject Repository
    private PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository;

    public PharmacyDrugAvailabilityServiceImpl(
        PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository
        ) {
        this.pharmacyDrugAvailabilityRepository = pharmacyDrugAvailabilityRepository;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public PharmacyDrugAvailabilityEntity savePharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability) {
        // save returns an Entity by default
        return pharmacyDrugAvailabilityRepository.save(pharmacyDrugAvailability);
    }

    @Override
    public Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable) {
        return pharmacyDrugAvailabilityRepository.findAllByDrug_LicenceNo(licenceNo, pageable);
    }

    @Override
    public List<PharmacyDrugAvailabilityEntity> findAllByPharmacy(PharmacyEntity pharmacy) {
        return pharmacyDrugAvailabilityRepository.findAllByPharmacy(pharmacy);
    }

    @Override
    public boolean existsByPharmacyAndDrug(PharmacyEntity pharmacy, DrugEntity drug) {
        return pharmacyDrugAvailabilityRepository.existsByPharmacyAndDrug(pharmacy, drug);
    }

    @Override
    public void delete(Long id) {
        pharmacyDrugAvailabilityRepository.deleteById(id);
    }
       
    @Transactional
    @Override
    public void deleteAllByPharmacy(PharmacyEntity pharmacy) {
        pharmacyDrugAvailabilityRepository.deleteAllByPharmacy(pharmacy);
    }

}
