package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<PharmacyEntity> findAll(Pageable pageable) {
        return pharmacyRepository.findAll(pageable);
    }

    @Override
    public Optional<PharmacyEntity> findOne(Integer psiRegNo) {
        return pharmacyRepository.findById(psiRegNo);
    }

    @Override
    public boolean isPresent(Integer psiRegNo) {
        return pharmacyRepository.existsById(psiRegNo);
    }

    @Override
    public PharmacyEntity partialUpdate(Integer psiRegNo, PharmacyEntity pharmacy) {
        // make sure psiRegNo of the pharmacy entity passed in is the same as the psiRegNo in the URL
        pharmacy.setPsiRegNo(psiRegNo);
        // retrieve record from the database
        return pharmacyRepository.findById(psiRegNo).map(existingRecord -> {
            // update pharmacy name (useful in the case of a sale/takeover/merge)
            Optional.ofNullable(pharmacy.getPharmacyName()).ifPresent(existingRecord::setPharmacyName);
            // update phone number
            Optional.ofNullable(pharmacy.getPhoneNo()).ifPresent(existingRecord::setPhoneNo);
            // do not support partial update of PSI Registration Number or Eircode, as these should stay constant
            // if either of these properties change, this would be a new different pharmacy and should be a new record
            return pharmacyRepository.save(existingRecord);
        }).orElseThrow(() -> new RuntimeException("Record does not exist."));
    }

    @Override
    public void delete(Integer psiRegNo) {
        pharmacyRepository.deleteById(psiRegNo);
    }

}
