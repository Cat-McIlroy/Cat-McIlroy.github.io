package com.cmcilroy.medicines_shortages_assistant.integration.services;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;
import com.cmcilroy.medicines_shortages_assistant.services.impl.PharmacyDrugAvailabilityServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PharmacyDrugAvailabilityServiceIntegrationTests {

    private PharmacyDrugAvailabilityServiceImpl availabilityService;

    private PharmacyDrugAvailabilityRepository availabilityRepository;

    private PharmacyRepository pharmacyRepository;

    private DrugRepository drugRepository;

    private DatabaseCleaner databaseCleaner;

    @Autowired
    public PharmacyDrugAvailabilityServiceIntegrationTests(
        PharmacyDrugAvailabilityServiceImpl availabilityService, 
        PharmacyDrugAvailabilityRepository availabilityRepository, 
        PharmacyRepository pharmacyRepository,
        DrugRepository drugRepository,
        DatabaseCleaner databaseCleaner
    ) {
        this.availabilityService = availabilityService;
        this.availabilityRepository = availabilityRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
        this.databaseCleaner = databaseCleaner;
    }

///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

///////////////////////////////////////// savePharmacyDrugAvailability() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testSavePharmacyDrugAvailability() {
        // create test pharmacy and drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();
        // persist them in the database
        pharmacyRepository.save(pharmacy);
        drugRepository.save(drug);
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        
        // call service method and store returned entity
        PharmacyDrugAvailabilityEntity savedAvailability = availabilityService.savePharmacyDrugAvailability(availability);

        // verify that the returned entity is not null
        assertNotNull(savedAvailability);
        // verify that the returned entity is identical to the test availability
        assertEquals(availability, savedAvailability);

    }

///////////////////////////////////////// findAllByLicenceNo() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByLicenceNo() {
        // create test pharmacy and drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();
        // persist them in the database
        pharmacyRepository.save(pharmacy);
        drugRepository.save(drug);
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // persist it in the database
        availabilityRepository.save(availability);

        // retrieve page of results
        Page<PharmacyDrugAvailabilityEntity> resultsPage = availabilityService
            .findAllByLicenceNo(drug.getLicenceNo(), PageRequest.of(0, 10));

        // verify that the results returned are not null
        assertNotNull(resultsPage);
        // verify that the results page returned is not empty, it should contain the test availability
        assertFalse(resultsPage.isEmpty());
        // verify that the results page returned contains the test drug
        assertEquals(drug, resultsPage.getContent().getFirst().getDrug());

    }

///////////////////////////////////////// findAllByPharmacy() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByPharmacy() {
        // create test pharmacy and drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();
        // persist them in the database
        pharmacyRepository.save(pharmacy);
        drugRepository.save(drug);
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // persist it in the database
        availabilityRepository.save(availability);

        // retrieve list of results from database
        List<PharmacyDrugAvailabilityEntity> results = availabilityService.findAllByPharmacy(pharmacy);

        // verify that the results are not null
        assertNotNull(results);
        // verify that the results list contains one element
        assertEquals(1, results.size());
        // verify that the element contained in results is the test availability
        assertEquals(availability, results.getFirst());

    }

///////////////////////////////////////// findAllByPharmacy() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testExistsByPharmacyAndDrug() {
        // create test pharmacy and drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();
        // persist them in the database
        pharmacyRepository.save(pharmacy);
        drugRepository.save(drug);
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // persist it in the database
        availabilityRepository.save(availability);

        boolean exists = availabilityService.existsByPharmacyAndDrug(pharmacy, drug);

        // verify that test availability exists in the database
        assertTrue(exists);

    }

///////////////////////////////////////// delete() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testDelete() {
        // create test pharmacy and drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();
        // persist them in the database
        pharmacyRepository.save(pharmacy);
        drugRepository.save(drug);
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // persist it in the database
        availabilityRepository.save(availability);
        // get id of test availability
        Long id = availability.getId();

        // call service method
        availabilityService.delete(id);

        // verify that the record no longer exists
        assertFalse(availabilityRepository.existsById(id));
    }

///////////////////////////////////////// deleteAllByPharmacy() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testDeleteAllByPharmacy() {
        // create test pharmacy and drug
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        DrugEntity drug = TestData.createTestDrugA();
        // persist them in the database
        pharmacyRepository.save(pharmacy);
        drugRepository.save(drug);
        // create test availability
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // persist it in the database
        availabilityRepository.save(availability);

        // call service method
        availabilityService.deleteAllByPharmacy(pharmacy);

        // verify that no records remain associated with the test pharmacy
        List<PharmacyDrugAvailabilityEntity> matchingRecords = availabilityRepository.findAllByPharmacy(pharmacy);
        assertTrue(matchingRecords.isEmpty());

    }

}
