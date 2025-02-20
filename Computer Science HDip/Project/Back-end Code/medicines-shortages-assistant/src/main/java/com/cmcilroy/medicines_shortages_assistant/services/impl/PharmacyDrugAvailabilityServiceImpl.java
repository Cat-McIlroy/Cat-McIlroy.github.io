package com.cmcilroy.medicines_shortages_assistant.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.scrapers.WebScraper;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;

@Service 
public class PharmacyDrugAvailabilityServiceImpl implements PharmacyDrugAvailabilityService{

    // inject Repositories
    private PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository;
    private PharmacyRepository pharmacyRepository;
    private DrugRepository drugRepository;

    // inject Pharmacy Service
    private PharmacyService pharmacyService;

    // inject Web Scraper
    private WebScraper webScraper;

    public PharmacyDrugAvailabilityServiceImpl(
        PharmacyDrugAvailabilityRepository pharmacyDrugAvailabilityRepository,
        PharmacyService pharmacyService,
        PharmacyRepository pharmacyRepository,
        DrugRepository drugRepository,
        WebScraper webScraper
        ) {
        this.pharmacyDrugAvailabilityRepository = pharmacyDrugAvailabilityRepository;
        this.pharmacyService = pharmacyService;
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
        this.webScraper = webScraper;
    }

    // pass-through method. The Service layer is taking the Entity and passing it to the Repository which persists it in the database
    @Override
    public PharmacyDrugAvailabilityEntity savePharmacyDrugAvailability(PharmacyDrugAvailabilityEntity pharmacyDrugAvailability) {
        // if PharmacyDrugAvailabilityEntity contains a nested Pharmacy object which does not yet exist in the database, persist it
        PharmacyEntity pharmacy = pharmacyDrugAvailability.getPharmacy();
        if(!pharmacyService.isPresent(pharmacy.getPsiRegNo())) {
            pharmacyService.savePharmacy(pharmacy.getPsiRegNo(), pharmacy);
        }
        // do not provide the same functionality for the Drug object, because all Irish authorised human medicines should already exist in the database
        // if the nested Drug object does not already exist in the database then it is either an error or is not on the authorised list

        // save returns an Entity by default
        return pharmacyDrugAvailabilityRepository.save(pharmacyDrugAvailability);
    }

    // run on start-up, but not until Drug and Pharmacy initial updates have taken place
    // therefore use Scheduled to trigger method 5 seconds after start-up, not PostConstruct
    @Override
    @Scheduled(fixedDelay = 5000) 
    public void initialUpdate() {
        // get all of pharmacies from database
        Iterable<PharmacyEntity> pharmacies = pharmacyRepository.findAll();
        // scrape HPRA website for medicines shortages
        List<String> shorts = webScraper.scrapeUnavailableDrugs();
        // for each drug in the list of shorts
        for(String shortDrug : shorts) {
            // find the corresponding drug in the database and store it in DrugEntity object
            Optional<DrugEntity> drug = drugRepository.findById(shortDrug);
            // then, for each pharmacy, generate a random availability
            for(PharmacyEntity pharmacy : pharmacies) {
                // generate random boolean value
                boolean isAvailable = Math.random() < 0.5;
                // if isAvailable, build a PharmacyDrugAvailabilityEntity and save it to the database
                if(isAvailable && drug.isPresent()) {
                    PharmacyDrugAvailabilityEntity availability = 
                        PharmacyDrugAvailabilityEntity.builder()
                        // id is automatically generated so don't declare here
                        .pharmacy(pharmacy)
                        .drug(drug.get())
                        .isAvailable(isAvailable)
                        .build();
                    pharmacyDrugAvailabilityRepository.save(availability);
                }
            }
        }
    }

    @Override
    public Page<PharmacyDrugAvailabilityEntity> findAll(Pageable pageable) {
        return pharmacyDrugAvailabilityRepository.findAll(pageable);
    }

    @Override
    public Page<PharmacyDrugAvailabilityEntity> findAllByLicenceNo(String licenceNo, Pageable pageable) {
        return pharmacyDrugAvailabilityRepository.findAllByLicenceNo(licenceNo, pageable);
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
    public PharmacyDrugAvailabilityEntity partialUpdate(Long id,
            PharmacyDrugAvailabilityEntity pharmacyDrugAvailability) {
        // set id of pharmacyDrugAvailability to be id in URL path, to ensure consistency
        pharmacyDrugAvailability.setId(id);
        // in order to update the desired attributes, first need to retrieve the record from the database
        return pharmacyDrugAvailabilityRepository.findById(id).map(existingRecord -> {
            // if the pharmacy drug availability entity passed in has a pharmacy drug availability property and is not null
            // update drug availability for that record
            Optional.ofNullable(pharmacyDrugAvailability.getIsAvailable()).ifPresent(existingRecord::setIsAvailable);
            // do not support partial update of associated Pharmacy or Drug, this should constitute a new record instead
            // save updates to existing record to the database and return updated pharmacy drug availability entity
            return pharmacyDrugAvailabilityRepository.save(existingRecord);
        }).orElseThrow(() -> new RuntimeException("Record does not exist."));
    }

    @Override
    public void delete(Long id) {
        pharmacyDrugAvailabilityRepository.deleteById(id);
    }

}
