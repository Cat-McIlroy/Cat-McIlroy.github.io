package com.cmcilroy.medicines_shortages_assistant.integration.repositories;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.PharmacyRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PharmacyRepositoryIntegrationTests {

    // class under test
    private PharmacyRepository pharmacyRepository;

    // inject DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    // constructor dependency injection
    @Autowired
    public PharmacyRepositoryIntegrationTests(PharmacyRepository pharmacyRepository, DatabaseCleaner databaseCleaner) {
        this.pharmacyRepository = pharmacyRepository;
        this.databaseCleaner = databaseCleaner;
    }


///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

///////////////////////////////////////////////// findByEmail() METHOD TEST ////////////////////////////////////////////////////

    @Test
    public void testFindByEmail() {
        // create test pharmacy and persist it
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        pharmacyRepository.save(pharmacy);

        // call method to find pharmacy by email and store it in an Optional
        Optional<PharmacyEntity> result = pharmacyRepository.findByEmail(pharmacy.getEmail());

        // expect that the result is present
        assertTrue(result.isPresent());

        // expect that the result matches the test pharmacy
        assertEquals(pharmacy, result.get());

    }

}