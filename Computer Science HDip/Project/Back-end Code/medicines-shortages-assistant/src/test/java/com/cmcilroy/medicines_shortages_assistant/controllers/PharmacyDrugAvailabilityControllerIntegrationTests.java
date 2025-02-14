package com.cmcilroy.medicines_shortages_assistant.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDrugAvailabilityDto;
import com.cmcilroy.medicines_shortages_assistant.domain.dto.PharmacyDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyDrugAvailabilityService;
import com.cmcilroy.medicines_shortages_assistant.services.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc // creates an instance of MockMvc and places it into the application context ready for use
public class PharmacyDrugAvailabilityControllerIntegrationTests {
    
    // inject MockMvc
    private MockMvc mockMvc;

    // and ObjectMapper
    private ObjectMapper objectMapper;

    // and Services
    private PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService;
    private PharmacyService pharmacyService;
    private DrugService drugService;

    @Autowired
    public PharmacyDrugAvailabilityControllerIntegrationTests(
        MockMvc mockMvc, 
        PharmacyDrugAvailabilityService pharmacyDrugAvailabilityService,
        PharmacyService pharmacyService,
        DrugService drugService
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.pharmacyDrugAvailabilityService = pharmacyDrugAvailabilityService;
        this.pharmacyService = pharmacyService;
        this.drugService = drugService;
    }

///////////////////////////////////////////////////////// CREATE METHOD TESTS ///////////////////////////////////////////////////////////

    // test to check that createPharmacyDrugAvailability method returns a HTTP 201 Created code
    @Test
    public void testThatCreatePharmacyDrugAvailabilityReturnsHttp201Created() throws Exception {
        // PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        // DrugDto drug = TestData.createTestDrugDtoA();
        PharmacyDrugAvailabilityDto pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityDtoA(null, null);
        String createPharmacyDrugAvailabilityJson = objectMapper.writeValueAsString(pharmacyDrugAvailability);
        mockMvc.perform(
        MockMvcRequestBuilders.post("/pharmacy-drug-availabilities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPharmacyDrugAvailabilityJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    // test to check that createPharmacyDrugAvailability method returns expected saved pharmacy drug availability
    @Test
    public void testThatCreatePharmacyDrugAvailabilityReturnsSavedPharmacyDrugAvailability() throws Exception {
        PharmacyDrugAvailabilityDto pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityDtoA(null, null);
        String createPharmacyDrugAvailabilityJson = objectMapper.writeValueAsString(pharmacyDrugAvailability);
        mockMvc.perform(
        MockMvcRequestBuilders.post("/pharmacy-drug-availabilities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPharmacyDrugAvailabilityJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacy").value(nullValue())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.drug").value(nullValue())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.isAvailable").value(pharmacyDrugAvailability.getIsAvailable())
        );
    }

///////////////////////////////////////////////////// FIND METHODS TESTS //////////////////////////////////////////////////////////

    // test to check that listAllPharmacyDrugAvailabilities method returns a HTTP 200 Ok code
    @Test
    public void testThatListAllPharmacyDrugAvailabilitiesReturnsHttp200() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacy-drug-availabilities")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that listAllPharmacyDrugAvailabilities method returns expected list of saved pharmacy drug availabilities
    @Test
    public void testThatListAllPharmacyDrugAvailabilitiesReturnsListOfAvailabilities() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.savePharmacy(pharmacy.getPsiRegNo(), pharmacy);
        DrugEntity drug = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.saveDrug(drug.getLicenceNo(), drug);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // save the new PharmacyDrugAvailability entity in the test database
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailability);
        // MockMvc Expects
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacy-drug-availabilities")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].pharmacy").value(pharmacy)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].drug").value(drug)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].isAvailable").value(true)
        );
    }

    // test to check that GetPharmacyDrugAvailability method returns a HTTP 200 Ok code when record is found
    @Test
    public void testThatGetPharmacyDrugAvailabilityReturnsHttp200WhenRecordExists() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.savePharmacy(pharmacy.getPsiRegNo(), pharmacy);
        DrugEntity drug = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.saveDrug(drug.getLicenceNo(), drug);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // save the new PharmacyDrugAvailability entity in the test database
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailability);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/" + pharmacyDrugAvailability.getId())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that GetPharmacyDrugAvailability method returns a HTTP 404 Not Found code when record doesn't exist
    @Test
    public void testThatGetPharmacyDrugAvailabilityReturnsHttp404WhenNoRecordExists() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    // test to check that GetPharmacyDrugAvailability method returns correct record when record exists
    @Test
    public void testThatGetPharmacyDrugAvailabilityReturnsCorrectRecordWhenRecordExists() throws Exception {
        PharmacyEntity pharmacy = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.savePharmacy(pharmacy.getPsiRegNo(), pharmacy);
        DrugEntity drug = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.saveDrug(drug.getLicenceNo(), drug);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityA(pharmacy, drug);
        // save the new PharmacyDrugAvailability entity in the test database
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailability);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/pharmacy-drug-availabilities/" + pharmacyDrugAvailability.getId())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacy").value(pharmacy)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.drug").value(drug)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.isAvailable").value(true)
        );
    }

////////////////////////////////////////////////////// UPDATE METHOD TESTS //////////////////////////////////////////////////////////

    // test to check that fullUpdatePharmacyDrugAvailability method returns a HTTP 404 Not Found code when record doesn't exist
    @Test
    public void testThatFullUpdatePharmacyDrugAvailabilityReturnsHttp404WhenNoRecordExists() throws Exception {
        PharmacyDto pharmacy = TestData.createTestPharmacyDtoA();
        DrugDto drug = TestData.createTestDrugDtoA();
        PharmacyDrugAvailabilityDto pharmacyDrugAvailability = TestData.createTestPharmacyDrugAvailabilityDtoA(pharmacy, drug);
        String pharmacyDrugAvailabilityJson = objectMapper.writeValueAsString(pharmacyDrugAvailability);
        mockMvc.perform(
            MockMvcRequestBuilders.put("/pharmacy-drug-availabilities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pharmacyDrugAvailabilityJson)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    // test to check that fullUpdatePharmacyDrugAvailability method returns a HTTP 200 Ok code when record exists
    @Test
    public void testThatFullUpdatePharmacyDrugAvailabilityReturnsHttp200WhenRecordExists() throws Exception {
        PharmacyEntity pharmacyEntity = TestData.createTestPharmacyA();
        // persist Pharmacy entity in the test database
        pharmacyService.savePharmacy(pharmacyEntity.getPsiRegNo(), pharmacyEntity);
        DrugEntity drugEntity = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.saveDrug(drugEntity.getLicenceNo(), drugEntity);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(pharmacyEntity, drugEntity);
        // save the new PharmacyDrugAvailability entity in the test database
        PharmacyDrugAvailabilityEntity savedPharmacyDrugAvailabilityEntity = 
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailabilityEntity);

        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoA();
        DrugDto drugDto = TestData.createTestDrugDtoA();
        PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto = TestData.createTestPharmacyDrugAvailabilityDtoA(pharmacyDto, drugDto);

        String pharmacyDrugAvailabilityJson = objectMapper.writeValueAsString(pharmacyDrugAvailabilityDto);
        mockMvc.perform(
            MockMvcRequestBuilders.put("/pharmacy-drug-availabilities/" + savedPharmacyDrugAvailabilityEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(pharmacyDrugAvailabilityJson)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that fullUpdatePharmacyDrugAvailability correctly updates existing record
    @Test
    public void testThatFullUpdatePharmacyDrugAvailabilityUpdatesExistingRecord() throws Exception {
        PharmacyEntity pharmacyEntityA = TestData.createTestPharmacyA();
        PharmacyEntity pharmacyEntityB = TestData.createTestPharmacyB();
        // persist Pharmacy entities in the test database
        pharmacyService.savePharmacy(pharmacyEntityA.getPsiRegNo(), pharmacyEntityA);
        pharmacyService.savePharmacy(pharmacyEntityB.getPsiRegNo(), pharmacyEntityB);
        DrugEntity drugEntityA = TestData.createTestDrugA();
        DrugEntity drugEntityB = TestData.createTestDrugB();
        // persist Drug entities in the test database
        drugService.saveDrug(drugEntityA.getLicenceNo(), drugEntityA);
        drugService.saveDrug(drugEntityB.getLicenceNo(), drugEntityB);
        PharmacyDrugAvailabilityEntity pharmacyDrugAvailabilityEntity = TestData.createTestPharmacyDrugAvailabilityA(pharmacyEntityA, drugEntityA);
        // save the new PharmacyDrugAvailability entity in the test database
        PharmacyDrugAvailabilityEntity savedPharmacyDrugAvailabilityEntity = 
        pharmacyDrugAvailabilityService.savePharmacyDrugAvailability(pharmacyDrugAvailabilityEntity);

        PharmacyDto pharmacyDto = TestData.createTestPharmacyDtoB();
        DrugDto drugDto = TestData.createTestDrugDtoB();
        PharmacyDrugAvailabilityDto pharmacyDrugAvailabilityDto = TestData.createTestPharmacyDrugAvailabilityDtoB(pharmacyDto, drugDto);
        // make sure id of Dto matches the id of the existing entity to be updated 
        pharmacyDrugAvailabilityDto.setId(savedPharmacyDrugAvailabilityEntity.getId());

        String pharmacyDrugAvailabilityDtoUpdateJson = objectMapper.writeValueAsString(pharmacyDrugAvailabilityDto);
        mockMvc.perform(
            MockMvcRequestBuilders.put("/pharmacy-drug-availabilities/" + savedPharmacyDrugAvailabilityEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(pharmacyDrugAvailabilityDtoUpdateJson)
        // expect properties to match those of pharmacyDrugAvailabilityDto as this was the update
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.pharmacy").value(pharmacyDrugAvailabilityDto.getPharmacy())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.drug").value(pharmacyDrugAvailabilityDto.getDrug())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.isAvailable").value(pharmacyDrugAvailabilityDto.getIsAvailable())
        );
    }
}
