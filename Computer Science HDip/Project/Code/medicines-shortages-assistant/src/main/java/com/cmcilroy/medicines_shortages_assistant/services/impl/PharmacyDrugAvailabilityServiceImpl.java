package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.Optional;

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

    // inject Repositories
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

    // run on start-up, but not until Drug and Pharmacy initial updates have taken place
    // therefore use Scheduled to trigger method 5 seconds after start-up, not PostConstruct
    // @Override
    // @Scheduled(fixedDelay = 5000) 
    // public void initialUpdate() {
    //     // get all of pharmacies from database
    //     Iterable<PharmacyEntity> pharmacies = pharmacyRepository.findAll();
    //     // scrape HPRA website for medicines shortages
    //     List<String> shorts = webScraper.scrapeUnavailableDrugs();
    //     // for each drug in the list of shorts
    //     for(String shortDrug : shorts) {
    //         // find the corresponding drug in the database and store it in DrugEntity object
    //         Optional<DrugEntity> drug = drugRepository.findById(shortDrug);
    //         // then, for each pharmacy, generate a random availability
    //         for(PharmacyEntity pharmacy : pharmacies) {
    //             // generate random boolean value
    //             boolean isAvailable = Math.random() < 0.5;
    //             // if isAvailable, build a PharmacyDrugAvailabilityEntity and save it to the database
    //             if(isAvailable && drug.isPresent()) {
    //                 PharmacyDrugAvailabilityEntity availability = 
    //                     PharmacyDrugAvailabilityEntity.builder()
    //                     // id is automatically generated so don't declare here
    //                     .pharmacy(pharmacy)
    //                     .drug(drug.get())
    //                     .isAvailable(isAvailable)
    //                     .build();
    //                 pharmacyDrugAvailabilityRepository.save(availability);
    //             }
    //         }
    //     }
    // }

    @Override
    public Page<PharmacyDrugAvailabilityEntity> findAll(Pageable pageable) {
        return pharmacyDrugAvailabilityRepository.findAll(pageable);
    }

    @Override
    public Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable) {
        return pharmacyDrugAvailabilityRepository.findAllByDrug_LicenceNo(licenceNo, pageable);
    }

    @Override
    public Page<PharmacyDrugAvailabilityEntity> findAllByPharmacy(PharmacyEntity pharmacy, Pageable pageable) {
        return pharmacyDrugAvailabilityRepository.findAllByPharmacy(pharmacy, pageable);
    }

    @Override
    public Optional<PharmacyDrugAvailabilityEntity> findOne(Long id) {
        return pharmacyDrugAvailabilityRepository.findById(id);
    }

    @Override
    public boolean isPresent(Long id) {
        return pharmacyDrugAvailabilityRepository.existsById(id);
    }

    @Override
    public boolean existsByPharmacyAndDrug(PharmacyEntity pharmacy, DrugEntity drug) {
        return pharmacyDrugAvailabilityRepository.existsByPharmacyAndDrug(pharmacy, drug);
    }

    @Override
    public PharmacyDrugAvailabilityEntity updateAvailability(Long id, boolean isAvailable) {
        // in order to update the availability, first need to retrieve the record from the database
        Optional<PharmacyDrugAvailabilityEntity> record = pharmacyDrugAvailabilityRepository.findById(id);
            // if no record found
            if(record.isEmpty()) {
                throw new RuntimeException("Record does not exist.");
            }
            // otherwise update drug availability for that record
            // do not support partial update of associated Pharmacy or Drug, this should constitute a new record instead
            record.get().setIsAvailable(isAvailable);
            // do not support partial update of associated Pharmacy or Drug, this should constitute a new record instead
            // save updates to existing record to the database and return updated pharmacy drug availability entity
            return record.get();
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
