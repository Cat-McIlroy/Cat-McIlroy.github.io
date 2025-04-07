package com.cmcilroy.medicines_shortages_assistant.unit.services;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;
import com.cmcilroy.medicines_shortages_assistant.services.impl.DrugServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DrugServiceTest {

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private DrugServiceImpl drugService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

/////////////////////////////////////// findAll() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAll() {
        // create test drugs
        DrugEntity drugA = TestData.createTestDrugA();
        DrugEntity drugB = TestData.createTestDrugB();

        // when this method is called, return a list of the test drugs
        when(drugRepository.findAll()).thenReturn(List.of(drugA, drugB));

        // call service method
        Iterable<DrugEntity> drugs = drugService.findAll();

        // verify that the drugRepository method is called once
        verify(drugRepository, times(1)).findAll();
        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list returned has a size of 2 elements
        assertTrue(drugs.spliterator().getExactSizeIfKnown() == 2);
    }

/////////////////////////////////////// findAllByActiveSubstance() METHOD TESTS /////////////////////////////////////////////////////////

    // test for findAllByActiveSubstance() method where active substance does not have synonyms
    @Test
    public void testFindAllByActiveSubstance() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();

        // when this method is called, return test drug
        when(drugRepository.findAllByContainsActiveSubstance("amlodipine")).thenReturn(List.of(drug));

        // call service method 
        List<DrugEntity> drugs = drugService.findAllByActiveSubstance("amlodipine");

        // verify that the drugRepository method is called once with the specified parameter
        verify(drugRepository, times(1)).findAllByContainsActiveSubstance("amlodipine");
        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list is not empty
        assertFalse(drugs.isEmpty());
        // verify that the list returned contains the test drug
        assertTrue(drugs.get(0).getActiveSubstance().equalsIgnoreCase("amlodipine"));
    }

    // test for findAllByActiveSubstance() method where active substance has synonyms
    @Test
    public void testFindAllByActiveSubstanceWithSynonyms() {
        // create two test drugs with different aspirin synonyms (aspirin, and acetylsalicylic acid)
        DrugEntity drugAspirin = TestData.createTestDrugD();
        DrugEntity drugAsa = TestData.createTestDrugE();

        // when this method is called, return a list containing the two test drugs
        when(drugRepository.findAllByContainsActiveSubstance("aspirin")).thenReturn(List.of(drugAspirin));
        when(drugRepository.findAllByContainsActiveSubstance("acetylsalicylic acid")).thenReturn(List.of(drugAsa));

        // call service method
        List<DrugEntity> drugs = drugService.findAllByActiveSubstance("aspirin");

        System.out.println(drugs);

        // verify that the drugRepository method is called twice with the specified parameters
        verify(drugRepository, times(1)).findAllByContainsActiveSubstance("aspirin");
        verify(drugRepository, times(1)).findAllByContainsActiveSubstance("acetylsalicylic acid");
        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list is not empty
        assertFalse(drugs.isEmpty());
        // verify that the list returned contains the two test drugs
        assertTrue(drugs.contains(drugAspirin));
        assertTrue(drugs.contains(drugAsa));
    }

/////////////////////////////////////// findAllByComboActiveSubstances() METHOD TESTS /////////////////////////////////////////////////////////

    // test for findAllByComboActiveSubstances() method where active substance does not have synonyms
    @Test
    public void testFindAllByComboActiveSubstances() {
        // create test drug which has combination substance
        DrugEntity drug = TestData.createTestDrugB();

        // when this method is called, return test drug
        when(drugRepository.findAll()).thenReturn(List.of(drug));

        // call service method 
        List<DrugEntity> drugs = drugService.findAllByComboActiveSubstances("amlodipine, olmesartan");

        // verify that the drugRepository method is called once with the specified parameter
        verify(drugRepository, times(1)).findAll();
        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list is not empty
        assertFalse(drugs.isEmpty());
        // verify that the list returned contains the test drug combination
        assertTrue(drugs.get(0).getActiveSubstance().toLowerCase().contains("amlodipine"));
        assertTrue(drugs.get(0).getActiveSubstance().toLowerCase().contains("olmesartan"));
    }

    // test for findAllByComboActiveSubstances() method where active substance has synonyms
    @Test
    public void testFindAllByComboActiveSubstancesHasSynonyms() {
        // create test drugs which are combination substances with different aspirin synonyms
        DrugEntity drugComboAspirin = TestData.createTestDrugF();
        DrugEntity drugComboAsa = TestData.createTestDrugG();

        // when this method is called, return test drug
        when(drugRepository.findAll()).thenReturn(List.of(drugComboAspirin, drugComboAsa));

        // call service method with combination of substances present in one of the test drugs (the other has same combo, different aspirin synonym)
        List<DrugEntity> drugs = drugService.findAllByComboActiveSubstances("aspirin, ramipril, atorvastatin");

        // verify that the drugRepository method is called once with the specified parameter
        verify(drugRepository, times(1)).findAll();
        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list is not empty
        assertFalse(drugs.isEmpty());
        // verify that the list returned contains the two test drugs
        assertTrue(drugs.contains(drugComboAspirin));
        assertTrue(drugs.contains(drugComboAsa));
    }

/////////////////////////////////////// findOne() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindOne() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();

        // when this method is called, return an Optional containing the test drug
        when(drugRepository.findById(drug.getLicenceNo())).thenReturn(Optional.of(drug));

        // call service method
        Optional<DrugEntity> result = drugService.findOne(drug.getLicenceNo());

        // verify that the drugRepository method is called once with the specified parameter
        verify(drugRepository, times(1)).findById(drug.getLicenceNo());
        // verify that the result is present
        assertTrue(result.isPresent());
        // verify that the result contains the test drug
        assertEquals(drug.getLicenceNo(), result.get().getLicenceNo());
    }

/////////////////////////////////////// findByContainsProductName() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindByContainsProductName() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();

        // when this method is called, return test drug
        when(drugRepository.findByContainsProductName("amlodipine")).thenReturn(List.of(drug));

        // call service method 
        List<DrugEntity> drugs = drugService.findByContainsProductName("amlodipine");

        // verify that the drugRepository method is called once with the specified parameter
        verify(drugRepository, times(1)).findByContainsProductName("amlodipine");
        // verify that the list returned is not null
        assertNotNull(drugs);
        // verify that the list is not empty
        assertFalse(drugs.isEmpty());
        // verify that the list returned contains the test drug
        assertTrue(drugs.get(0).getProductName().equalsIgnoreCase(drug.getProductName()));
    }

/////////////////////////////////////// findAllByIsAvailable() METHOD TEST /////////////////////////////////////////////////////////

    @Test
    public void testFindAllByIsAvailable() {
        // create test drug
        DrugEntity drug = TestData.createTestDrugA();

        // when this method is called, return test drug
        when(drugRepository.findAllByIsAvailable(true)).thenReturn(List.of(drug));

        // call service method
        Iterable<DrugEntity> result = drugService.findAllByIsAvailable(true);

        // verify that the drugRepository method is called once with the specified parameter
        verify(drugRepository, times(1)).findAllByIsAvailable(true);
        // verify that the list returned is not null
        assertNotNull(result);
        // verify that the list returned has a size of 1 element
        assertTrue(result.spliterator().getExactSizeIfKnown() == 1);
    }

}