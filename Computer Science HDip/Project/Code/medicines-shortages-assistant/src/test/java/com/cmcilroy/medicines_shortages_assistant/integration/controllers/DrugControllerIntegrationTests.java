package com.cmcilroy.medicines_shortages_assistant.integration.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.repositories.DrugRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc 
public class DrugControllerIntegrationTests {

    // inject MockMvc
    private MockMvc mockMvc;

    // and Repository 
    private DrugRepository drugRepository;

    // and DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    @Autowired
    public DrugControllerIntegrationTests(
        MockMvc mockMvc, DrugRepository drugRepository, DatabaseCleaner databaseCleaner
    ) {
        this.mockMvc = mockMvc;
        this.drugRepository = drugRepository;
        this.databaseCleaner = databaseCleaner;
    }

///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

/////////////////////////////////////// listAllShorts() METHOD TESTS /////////////////////////////////////////////////////////

    // test for listAllShorts() method when list contains both an Available drug and a Short drug, it has results
    @Test
    public void testListAllShortsHasResults() throws Exception {
        // create test drugs
        DrugEntity drugAvailable = TestData.createTestDrugA();
        DrugEntity drugShort = TestData.createTestDrugC();
        // persist the drugs in the database
        drugRepository.save(drugAvailable);
        drugRepository.save(drugShort);

        // perform GET request and validate the response
        mockMvc.perform(get("/drugs/shortages")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // expect that only one DrugDto is returned and it corresponds to drugShort
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].licenceNo", is(drugShort.getLicenceNo())));

    }

    // test for listAllShorts() method when list contains only Available drugs and no Shorts, it has no results
    @Test
    public void testListAllShortsAvailableNoResults() throws Exception {
        // create test drugs
        DrugEntity drugA = TestData.createTestDrugA();
        DrugEntity drugB = TestData.createTestDrugB();
        // persist the drugs in the database
        drugRepository.save(drugA);
        drugRepository.save(drugB);

        // perform GET request and validate the response
        mockMvc.perform(get("/drugs/shortages")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // expect that there is an empty list returned
                .andExpect(jsonPath("$", hasSize(0)));

    }

/////////////////////////////////////// listDrugNamesByActiveSubstance() METHOD TESTS /////////////////////////////////////////////////////////

    // test for listDrugNamesByActiveSubstance() method when no results are returned
    @Test
    public void testListDrugNamesByActiveSubstanceNoResults() throws Exception {
        // create a test drug
        DrugEntity drug = TestData.createTestDrugA();
        // persist it in the database
        drugRepository.save(drug);
        // declare an active substance different to the test drug
        String activeSubstance = "bumetanide";

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "page")
                .param("size", "size"))
                // expect to receive a HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect response is an empty string
                .andExpect(content().string(""));

    }

    // test for listDrugNamesByActiveSubstance() method with a single active substance
    @Test
    public void testListDrugNamesByActiveSubstanceSingleSubstance() throws Exception {
        // create a test drug
        DrugEntity drug = TestData.createTestDrugA();
        // persist it in the database
        drugRepository.save(drug);
        // declare an active substance the same as the test drug
        String activeSubstance = "amlodipine";

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto object is contained in the response
                .andExpect(jsonPath("$.content[0].licenceNo", is(drug.getLicenceNo())));

    }


    // test for listDrugNamesByActiveSubstance() method when single active substance has synonyms
    @Test
    public void testListDrugNamesByActiveSubstanceSingleSubstanceSynonyms() throws Exception {
        // declare an active substance which has synonyms
        String activeSubstance = "aspirin";
        // create two test drugs with different aspirin synonyms (aspirin, and acetylsalicylic acid)
        DrugEntity drugAspirin = TestData.createTestDrugD();
        DrugEntity drugAsa = TestData.createTestDrugE();
        // persist both drugs in the database
        drugRepository.save(drugAspirin);
        drugRepository.save(drugAsa);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected drugs are contained in the response
                .andExpect(jsonPath("$.content[*].licenceNo", containsInAnyOrder(
                        drugAspirin.getLicenceNo(),
                        drugAsa.getLicenceNo()
                    )));
    }

    // test for listDrugNamesByActiveSubstance() method with a combination active substance
    @Test
    public void testListDrugNamesByActiveSubstanceCombinationSubstance() throws Exception {
        // create a test drug containing a combination active substance
        DrugEntity drug = TestData.createTestDrugB();
        // persist it in the database
        drugRepository.save(drug);
        // declare an active substance combination which is present in the test drug
        String activeSubstance = "amlodipine, olmesartan";

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected DrugDto object is contained in the response
                .andExpect(jsonPath("$.content[0].licenceNo", is(drug.getLicenceNo())));

    }

    // test for listDrugNamesByActiveSubstance() method when combination active substance has synonyms
    @Test
    public void testListDrugNamesByActiveSubstanceCombinationSubstanceSynonyms() throws Exception {
        // declare a combination active substance which has synonyms (e.g. contains aspirin, AKA acetylsalicylic acid).
        String activeSubstance = "aspirin, ramipril, atorvastatin";
        // create two test combination drugs with different aspirin synonyms (aspirin, and acetylsalicylic acid)
        DrugEntity drugComboAsa = TestData.createTestDrugF();
        DrugEntity drugComboAspirin = TestData.createTestDrugG();
        // persist both drugs in the database
        drugRepository.save(drugComboAsa);
        drugRepository.save(drugComboAspirin);

        // perform GET request with active substance and validate the response
        mockMvc.perform(get("/drugs/search-by-active-substance")
                .accept(MediaType.APPLICATION_JSON)
                .param("activeSubstance", activeSubstance)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that both expected drugs are contained in the response
                .andExpect(jsonPath("$.content[*].licenceNo", containsInAnyOrder(
                        drugComboAsa.getLicenceNo(),
                        drugComboAspirin.getLicenceNo()
                    )));

    }

/////////////////////////////////////// listAlternativeDrugNames() METHOD TESTS /////////////////////////////////////////////////////////

    // test for listAlternativeDrugNames() method where no results are returned
    @Test
    public void testListAlternativeDrugNamesNoResults() throws Exception {
        // create a test drug
        DrugEntity drug = TestData.createTestDrugA();
        // persist the drug in the database
        drugRepository.save(drug);
        // declare a product name which is non-existent
        String productName = "non-existent";

        // perform GET request for alternative drug names and validate the response
        mockMvc.perform(get("/drugs/search-by-product-name")
                .accept(MediaType.APPLICATION_JSON)
                .param("productName", productName)
                .param("page", "0")
                .param("size", "10"))
                // expect to receive a HTTP Status 404 Not Found
                .andExpect(status().isNotFound())
                // expect response is an empty string
                .andExpect(content().string(""));

    }

    // test for listAlternativeDrugNames() method where results are returned
    @Test
    public void testListAlternativeDrugNamesHasResults() throws Exception {
        // declare a product name which is a brand name and has existing generic and brand alternatives
        String productName = "istin";
        // create two test drugs which are different brands containing the same active substance,
        // one that contains a combination with the same active in it, and one that contains a different single active
        DrugEntity drugIstinBrand = TestData.createTestDrugH();
        DrugEntity drugGenericBrand = TestData.createTestDrugA();
        DrugEntity drugComboContainingActive = TestData.createTestDrugB();
        DrugEntity drugDifferentActive = TestData.createTestDrugD();
        // persist all drugs in the database
        drugRepository.save(drugIstinBrand);
        drugRepository.save(drugGenericBrand);
        drugRepository.save(drugComboContainingActive);
        drugRepository.save(drugDifferentActive);

        // perform GET request for alternative drug names and validate the response
        mockMvc.perform(get("/drugs/search-by-product-name")
                .accept(MediaType.APPLICATION_JSON)
                .param("productName", productName)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // check that the expected drugs are contained in the response
                .andExpect(jsonPath("$.content[*].licenceNo", containsInAnyOrder(
                        drugIstinBrand.getLicenceNo(),
                        drugGenericBrand.getLicenceNo()
                    )));

    }
}