package com.cmcilroy.medicines_shortages_assistant.integration.repositories;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyDrugAvailabilityRepository;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PharmacyDrugAvailabilityRepositoryIntegrationTests {

    // class under test
    private PharmacyDrugAvailabilityRepository availabilityRepository;

    // repository classes
    private PharmacyRepository pharmacyRepository;
    private DrugRepository drugRepository;

    // inject DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    // constructor dependency injection
    @Autowired
    public PharmacyDrugAvailabilityRepositoryIntegrationTests(
    PharmacyDrugAvailabilityRepository availabilityRepository, 
    PharmacyRepository pharmacyRepository, 
    DrugRepository drugRepository,
    DatabaseCleaner databaseCleaner
    ) {
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

///////////////////////////////////////////////// findAllByDrug_LicenceNo() METHOD TEST ////////////////////////////////////////////////////

    @Test
    public void testFindAllByDrug_LicenceNo() {
        // create test drug and pharmacy and persist them in the database
        DrugEntity drug = TestData.createTestDrugA();
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        drugRepository.save(drug);
        pharmacyRepository.save(pharmacy);
        // create test availability and persist it in the database
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        availabilityRepository.save(availability);

        // call method to return page of results
        Page<PharmacyDrugAvailabilityEntity> resultsPage = availabilityRepository.findAllByDrug_LicenceNo(
                drug.getLicenceNo(), PageRequest.of(0, 10));

        // expect that the page returned contains a total of one element
        assertEquals(1, resultsPage.getTotalElements());
        // expect that the page contents contains the test availability
        assertEquals(availability, resultsPage.getContent().getFirst());

    }

///////////////////////////////////////////////// findAllByPharmacy() METHOD TEST ////////////////////////////////////////////////////

    @Test
    public void testFindAllByPharmacy() {
        // create test drug and pharmacy and persist them in the database
        DrugEntity drug = TestData.createTestDrugA();
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        drugRepository.save(drug);
        pharmacyRepository.save(pharmacy);
        // create test availability and persist it in the database
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        availabilityRepository.save(availability);

        // call method to return list of results
        List<PharmacyDrugAvailabilityEntity> results = availabilityRepository.findAllByPharmacy(pharmacy);

        // expect results list to contain a total of one element
        assertEquals(1, results.size());
        // expect the one element to match the test availability
        assertEquals(availability, results.get(0));

    }

///////////////////////////////////////////////// existsByPharmacyAndDrug() METHOD TEST ////////////////////////////////////////////////////

    @Test
    public void testExistsByPharmacyAndDrug() {
        // create test drug and pharmacy and persist them in the database
        DrugEntity drug = TestData.createTestDrugA();
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        drugRepository.save(drug);
        pharmacyRepository.save(pharmacy);
        // create test availability and persist it in the database
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        availabilityRepository.save(availability);

        // call method to return boolean
        boolean exists = availabilityRepository.existsByPharmacyAndDrug(pharmacy, drug);

        // expect record to exist in the database
        assertTrue(exists);

    }

///////////////////////////////////////////////// deleteAllByPharmacy() METHOD TEST ////////////////////////////////////////////////////

    @Test
    public void testDeleteAllByPharmacy() {
        // create test drug and pharmacy and persist them in the database
        DrugEntity drug = TestData.createTestDrugA();
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        drugRepository.save(drug);
        pharmacyRepository.save(pharmacy);
        // create test availability and persist it in the database
        PharmacyDrugAvailabilityEntity availability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        availabilityRepository.save(availability);

        // call method to return list of results
        List<PharmacyDrugAvailabilityEntity> resultsBeforeDeletion = availabilityRepository.findAllByPharmacy(pharmacy);

        // call method to delete availability
        availabilityRepository.deleteAllByPharmacy(pharmacy);

        // call method to return list of results
        List<PharmacyDrugAvailabilityEntity> resultsAfterDeletion = availabilityRepository.findAllByPharmacy(pharmacy);

        // expect results list before deletion to contain a total of one element
        assertEquals(1, resultsBeforeDeletion.size());
        // expect the one element to match the test availability
        assertEquals(availability, resultsBeforeDeletion.get(0));
        // expect results list after deletion to be empty
        assertTrue(resultsAfterDeletion.isEmpty());
    }

}