package com.cmcilroy.medicines_shortages_assistant.integration.repositories;

import java.util.List;

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
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DrugRepositoryIntegrationTests {

    // class under test
    private DrugRepository drugRepository;

    // inject DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    // constructor dependency injection
    @Autowired
    public DrugRepositoryIntegrationTests(DrugRepository drugRepository, DatabaseCleaner databaseCleaner) {
        this.drugRepository = drugRepository;
        this.databaseCleaner = databaseCleaner;
    }


///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

////////////////////////////////////////////// findAllByContainsActiveSubstance() METHOD TEST /////////////////////////////////////////////////


   @Test
    public void testFindAllByContainsActiveSubstance() {
        // persist test drugs in the database - Drug A, Drug B and Drug H contain active substance amlodipine
        drugRepository.saveAll(List.of(
            TestData.createTestDrugA(),
            TestData.createTestDrugB(),
            TestData.createTestDrugC(),
            TestData.createTestDrugH()
        ));
        // call method to return list of results
        List<DrugEntity> results = drugRepository.findAllByContainsActiveSubstance("amlodipine");
        // expect results list to contain three elements
        assertEquals(3, results.size());
        // expect the results in the list to both contain amlodipine as an active substance
        assertTrue(results.get(0).getActiveSubstance().toLowerCase().contains("amlodipine"));
        assertTrue(results.get(1).getActiveSubstance().toLowerCase().contains("amlodipine"));
        assertTrue(results.get(2).getActiveSubstance().toLowerCase().contains("amlodipine"));
        // expect the three elements to correspond to test Drug A, B and H
        assertTrue(results.contains(TestData.createTestDrugA()));
        assertTrue(results.contains(TestData.createTestDrugB()));
        assertTrue(results.contains(TestData.createTestDrugH()));

    }

////////////////////////////////////////////// findByContainsProductName() METHOD TEST /////////////////////////////////////////////////

    @Test
    public void testFindByContainsProductName() {
        // persist test drugs in the database - Drug F and Drug G contain "trinomia" in the product name
        drugRepository.saveAll(List.of(
            TestData.createTestDrugA(),
            TestData.createTestDrugB(),
            TestData.createTestDrugF(),
            TestData.createTestDrugG()
        ));
        // call method to return list of results
        List<DrugEntity> results = drugRepository.findByContainsProductName("trinomia");
        // expect results list to contain two elements
        assertEquals(2, results.size());
        // expect the results in the list to both contain trinomia in the product name
        assertTrue(results.get(0).getProductName().toLowerCase().contains("trinomia"));
        assertTrue(results.get(1).getProductName().toLowerCase().contains("trinomia"));
        // expect the two elements to match test Drug F and G
        assertTrue(results.contains(TestData.createTestDrugF()));
        assertTrue(results.contains(TestData.createTestDrugG()));

    }

////////////////////////////////////////////// findAllByIsAvailable() METHOD TEST /////////////////////////////////////////////////

    @Test
    public void testFindAllByIsAvailable() {
        // persist test drugs in the database - Drug A and Drug B are available, Drug C is unavailable
        drugRepository.saveAll(List.of(
            TestData.createTestDrugA(),
            TestData.createTestDrugB(),
            TestData.createTestDrugC()
        ));
        // call method to return iterable of results
        Iterable<DrugEntity> results = drugRepository.findAllByIsAvailable(true);
        // cast Iterable to List
        List<DrugEntity> resultsList = (List<DrugEntity>) results;
        // expect results list to contain two elements
        assertEquals(2, resultsList.size());
        // expect the results in the list to both have availability true
        assertTrue(resultsList.get(0).getIsAvailable().equals(true));
        assertTrue(resultsList.get(1).getIsAvailable().equals(true));
        // expect the two elements to match test Drug A and B
        assertTrue(resultsList.contains(TestData.createTestDrugA()));
        assertTrue(resultsList.contains(TestData.createTestDrugB()));

    }

}