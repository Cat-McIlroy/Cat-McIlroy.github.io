package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;

import jakarta.annotation.PostConstruct;

@Service
public class PharmacyServiceImpl implements PharmacyService{

    // inject Repositories
    private PharmacyRepository pharmacyRepository;
    private PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository;

    public PharmacyServiceImpl(PharmacyRepository pharmacyRepository, PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyDrugAvailabilityRepository = pharmacyDrugAvailabilityRepository;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public PharmacyEntity savePharmacy(Integer psiRegNo, PharmacyEntity pharmacy) {
        // ensure the psiRegNo associated with the pharmacy object to be saved is the same as the psiRegNo in the URL
        pharmacy.setPsiRegNo(psiRegNo);
        // save returns an Entity by default
        return pharmacyRepository.save(pharmacy);
    }

    // method to populate the database with a set of fictional pharmacies, for app demonstration purposes
    @Override
    @PostConstruct
    public void initialUpdate() {
    
        // clear database from any previous application runs
        pharmacyDrugAvailabilityRepository.deleteAll();
        pharmacyRepository.deleteAll();

        Random random = new Random();
        // build pharmacies called Pharmacy A through Pharmacy Z
        // ascii codes 65 through 90
        for(int i = 65; i <= 90; i++) {
            PharmacyEntity pharmacy = PharmacyEntity.builder()
                    // generate a random Integer to use as the psiRegNo
                    .psiRegNo(Integer.valueOf(random.nextInt(10000) + 1000))
                    .pharmacyName("Pharmacy " + (char) i)
                    .eircode(String.valueOf((char) i).repeat(7))
                    // generate a random phone number starting with 01
                    .phoneNo(String.format("01%d %03d %04d", random.nextInt(900) + 100, random.nextInt(900) + 100, random.nextInt(10000)))
                    .build();
            // save the pharmacy to the database
            pharmacyRepository.save(pharmacy);
        }
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
