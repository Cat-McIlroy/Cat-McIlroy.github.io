package com.cmcilroy.medicines_shortages_assistant.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyDrugAvailabilityEntity;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.PharmacyEntity;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc // creates an instance of MockMvc and places it into the application context ready for use
public class DrugControllerIntegrationTests {

    // inject MockMvc
    private MockMvc mockMvc;

    // and ObjectMapper
    private ObjectMapper objectMapper;

    // and DrugService 
    private DrugService drugService;

    @Autowired
    public DrugControllerIntegrationTests(MockMvc mockMvc, DrugService drugService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.drugService = drugService;
    }

    // test to check that createDrug method returns a HTTP 201 Created code
    @Test
    public void testThatCreateDrugReturnsHttp201Created() throws Exception{
        DrugDto drug = TestData.createTestDrugDtoA();
        String createDrugJson = objectMapper.writeValueAsString(drug);
        mockMvc.perform(
        MockMvcRequestBuilders.put("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
            .contentType(MediaType.APPLICATION_JSON)
            .content(createDrugJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    // test to check that createDrug method returns expected saved drug
    @Test
    public void testThatCreateDrugReturnsSavedDrug() throws Exception{
        DrugDto drug = TestData.createTestDrugDtoA();
        String createDrugJson = objectMapper.writeValueAsString(drug);
        mockMvc.perform(
        MockMvcRequestBuilders.put("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
            .contentType(MediaType.APPLICATION_JSON)
            .content(createDrugJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.licenceNo").value(drug.getLicenceNo())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.productName").value(drug.getProductName())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.strength").value(drug.getStrength())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.activeSubstance").value(drug.getActiveSubstance())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.isAvailable").value(drug.getIsAvailable())
        );
    }

    // test to check that listAllDrugs method returns a HTTP 200 Ok code
    @Test
    public void testThatListAllDrugsReturnsHttp200Ok() throws Exception{
        mockMvc.perform(
        MockMvcRequestBuilders.get("/drugs")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that listAllDrugs method returns expected drug
    @Test
    public void testThatListAllDrugsReturnsDrug() throws Exception{
        DrugEntity drug = TestData.createTestDrugA();
        drugService.createDrug(drug.getLicenceNo(),drug);
        mockMvc.perform(
        MockMvcRequestBuilders.get("/drugs")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].licenceNo").value("PA0749/067/001")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].productName").value("Amlodipine Teva 5 mg Tablets")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].strength").value("5 mg")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].activeSubstance").value("Amlodipine")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].isAvailable").value(true)
        );
    }

   // test to check that GetDrug method returns a HTTP 200 Ok code when record is found
    @Test
    public void testThatGetDrugReturnsHttp200WhenRecordExists() throws Exception {
        DrugEntity drug = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.createDrug(drug.getLicenceNo(), drug);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that GetDrug method returns a HTTP 404 Not Found code when record doesn't exist
    @Test
    public void testThatGetDrugReturnsHttp404WhenNoRecordExists() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/drugs/nonExistentLicenceNumber")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    // test to check that GetDrug method returns correct record when record exists
    @Test
    public void testThatGetDrugReturnsCorrectRecordWhenRecordExists() throws Exception {
        DrugEntity drug = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.createDrug(drug.getLicenceNo(), drug);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.licenceNo").value("PA0749/067/001")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.productName").value("Amlodipine Teva 5 mg Tablets")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.strength").value("5 mg")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.activeSubstance").value("Amlodipine")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.isAvailable").value(true)
        );
    }
}
