package com.cmcilroy.medicines_shortages_assistant.integration.services;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.services.impl.DrugServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DrugServiceIntegrationTests {

    private DrugServiceImpl drugService;

    private DrugRepository drugRepository;
 
    private DatabaseCleaner databaseCleaner;

    @Autowired
    public DrugServiceIntegrationTests(
        DrugServiceImpl drugService, DrugRepository drugRepository, DatabaseCleaner databaseCleaner
    ) {
        this.drugService = drugService;
        this.drugRepository = drugRepository;
        this.databaseCleaner = databaseCleaner;
    }

///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

///////////////////////////////////////// findAll() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAll() {
        // create test drugs
        DrugEntity drugA = TestData.createTestDrugA();
        DrugEntity drugB = TestData.createTestDrugB();

        // save test drugs to the repository
        drugRepository.save(drugA);
        drugRepository.save(drugB);

        // call service method
        Iterable<DrugEntity> drugs = drugService.findAll();

        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list returned has the correct size
        assertTrue(drugs.spliterator().getExactSizeIfKnown() == 2);

    }

/////////////////////////////////////// findAllByActiveSubstance() METHOD TESTS /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByActiveSubstance() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);

        // call service method
        List<DrugEntity> drugs = drugService.findAllByActiveSubstance("amlodipine");

        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list is not empty
        assertFalse(drugs.isEmpty());
        // verify that the list contains the correct drug
        assertTrue(drugs.get(0).getActiveSubstance().equalsIgnoreCase("amlodipine"));

    }

    @Test
    public void testFindAllByActiveSubstanceWithSynonyms() {
        // create two test drugs with different aspirin synonyms
        DrugEntity drugAspirin = TestData.createTestDrugD();
        DrugEntity drugAsa = TestData.createTestDrugE();

        drugRepository.save(drugAspirin);
        drugRepository.save(drugAsa);

        // call service method
        List<DrugEntity> drugs = drugService.findAllByActiveSubstance("aspirin");

        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list contains the two test drugs
        assertTrue(drugs.contains(drugAspirin));
        assertTrue(drugs.contains(drugAsa));

    }

/////////////////////////////////////// findAllByComboActiveSubstances() METHOD TESTS /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByComboActiveSubstances() {
        // create test drug which has combination substance
        DrugEntity drug = TestData.createTestDrugB();
        drugRepository.save(drug);

        // call service method 
        List<DrugEntity> drugs = drugService.findAllByComboActiveSubstances("amlodipine, olmesartan");

        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list contains the drug combination
        assertTrue(drugs.get(0).getActiveSubstance().toLowerCase().contains("amlodipine"));
        assertTrue(drugs.get(0).getActiveSubstance().toLowerCase().contains("olmesartan"));

    }

    @Test
    public void testFindAllByComboActiveSubstancesHasSynonyms() {
        // create test drugs which are combination substances with different aspirin synonyms
        DrugEntity drugComboAspirin = TestData.createTestDrugF();
        DrugEntity drugComboAsa = TestData.createTestDrugG();

        drugRepository.save(drugComboAspirin);
        drugRepository.save(drugComboAsa);

        // call service method
        List<DrugEntity> drugs = drugService.findAllByComboActiveSubstances("aspirin, ramipril, atorvastatin");

        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list contains the two test drugs
        assertTrue(drugs.contains(drugComboAspirin));
        assertTrue(drugs.contains(drugComboAsa));

    }

/////////////////////////////////////// findOne() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindOne() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);

        // call service method
        Optional<DrugEntity> result = drugService.findOne(drug.getLicenceNo());

        // verify that the result is present
        assertTrue(result.isPresent());
        // verify that the result contains the correct drug
        assertEquals(drug.getLicenceNo(), result.get().getLicenceNo());

    }

/////////////////////////////////////// findByContainsProductName() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindByContainsProductName() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);

        // call service method 
        List<DrugEntity> drugs = drugService.findByContainsProductName("amlodipine");

        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list contains the test drug
        assertTrue(drugs.get(0).getProductName().equalsIgnoreCase(drug.getProductName()));

    }

/////////////////////////////////////// findAllByIsAvailable() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByIsAvailable() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();
        drugRepository.save(drug);

        // call service method
        Iterable<DrugEntity> result = drugService.findAllByIsAvailable(true);

        // verify that the list returned is not null
        assertNotNull(result);
        // verify that the list has the correct size
        assertTrue(result.spliterator().getExactSizeIfKnown() == 1);

    }

}