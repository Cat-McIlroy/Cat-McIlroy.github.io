package com.cmcilroy.medicines_shortages_assistant.controllers;

// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.cmcilroy.medicines_shortages_assistant.TestData;
import com.cmcilroy.medicines_shortages_assistant.cleaner.DatabaseCleaner;
// import com.cmcilroy.medicines_shortages_assistant.domain.dto.DrugDto;
import com.cmcilroy.medicines_shortages_assistant.domain.entities.DrugEntity;
import com.cmcilroy.medicines_shortages_assistant.services.DrugService;
// import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc // creates an instance of MockMvc and places it into the application context ready for use
public class DrugControllerIntegrationTests {

    // inject MockMvc
    private MockMvc mockMvc;

    // and ObjectMapper
    // private ObjectMapper objectMapper;

    // and DrugService 
    private DrugService drugService;

    // and DatabaseCleaner
    private DatabaseCleaner databaseCleaner;

    @Autowired
    public DrugControllerIntegrationTests(MockMvc mockMvc, DrugService drugService, DatabaseCleaner databaseCleaner) {
        this.mockMvc = mockMvc;
        // this.objectMapper = new ObjectMapper();
        this.drugService = drugService;
        this.databaseCleaner = databaseCleaner;
    }

///////////////////////////////////////////////// CLEAR DATABASE BEFORE EACH TEST ////////////////////////////////////////////////////

    @BeforeEach
    public void clearDatabase() {
        databaseCleaner.clearDatabase();
    }

///////////////////////////////////////////////// CREATE & UPDATE METHOD TESTS ///////////////////////////////////////////////////////

    // test to check that createUpdateDrug method returns a HTTP 201 Created code on creating new record
    // @Test
    // public void testThatCreateDrugReturnsHttp201Created() throws Exception{
    //     DrugDto drug = TestData.createTestDrugDtoA();
    //     String drugJson = objectMapper.writeValueAsString(drug);
    //     mockMvc.perform(
    //     MockMvcRequestBuilders.put("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isCreated()
    //     );
    // }

    // test to check that createUpdateDrug method returns a HTTP 200 Ok code on updating existing record
    // @Test
    // public void testThatUpdateDrugReturnsHttp200Ok() throws Exception{
    //     DrugEntity drugEntity = TestData.createTestDrugA();
    //     DrugEntity savedDrugEntity = drugService.saveDrug(drugEntity.getLicenceNo(), drugEntity);
    //     DrugDto drugDto = TestData.createTestDrugDtoA();
    //     // make sure licence numbers of savedDrugEntity and drugDto match 
    //     drugDto.setLicenceNo(savedDrugEntity.getLicenceNo());
    //     String drugJson = objectMapper.writeValueAsString(drugDto);
    //     mockMvc.perform(
    //     MockMvcRequestBuilders.put("/drugs/" + URLEncoder.encode(drugDto.getLicenceNo(), StandardCharsets.UTF_8))
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isOk()
    //     );
    // }

    // test to check that createUpdateDrug method returns expected created drug
    // @Test
    // public void testThatCreateDrugReturnsCreatedDrug() throws Exception{
    //     DrugDto drug = TestData.createTestDrugDtoA();
    //     String drugJson = objectMapper.writeValueAsString(drug);
    //     mockMvc.perform(
    //     MockMvcRequestBuilders.put("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.licenceNo").value(drug.getLicenceNo())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.productName").value(drug.getProductName())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.manufacturer").value(drug.getManufacturer())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.strength").value(drug.getStrength())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.dosageForm").value(drug.getDosageForm())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.activeSubstance").value(drug.getActiveSubstance())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.isAvailable").value(drug.getIsAvailable())
    //     );
    // }

    // test to check that createUpdateDrug method returns expected updated drug
    // @Test
    // public void testThatUpdateDrugReturnsUpdatedDrug() throws Exception{
    //     DrugEntity drugEntity = TestData.createTestDrugA();
    //     DrugEntity savedDrugEntity = drugService.saveDrug(drugEntity.getLicenceNo(), drugEntity);
    //     DrugDto drugDto = TestData.createTestDrugDtoA();
    //     // make sure licence numbers of savedDrugEntity and drugDto match 
    //     drugDto.setLicenceNo(savedDrugEntity.getLicenceNo());
    //     // change product name property, to test update functionality
    //     drugDto.setProductName("UPDATED PRODUCT NAME");
    //     String drugJson = objectMapper.writeValueAsString(drugDto);
    //     mockMvc.perform(
    //     MockMvcRequestBuilders.put("/drugs/" + URLEncoder.encode(drugDto.getLicenceNo(), StandardCharsets.UTF_8))
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.licenceNo").value(drugDto.getLicenceNo())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.productName").value(drugDto.getProductName())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.manufacturer").value(drugDto.getManufacturer())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.strength").value(drugDto.getStrength())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.dosageForm").value(drugDto.getDosageForm())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.activeSubstance").value(drugDto.getActiveSubstance())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.isAvailable").value(drugDto.getIsAvailable())
    //     );
    // }

//////////////////////////////////////////////////////// PARTIAL UPDATE METHOD TESTS /////////////////////////////////////////////////////////

    // test to check that partialUpdateDrug method returns a HTTP 404 Not Found code when record doesn't exist
    // @Test
    // public void testThatPartialUpdateDrugReturnsHttp404WhenNoRecordExists() throws Exception {
    //     DrugDto drug = TestData.createTestDrugDtoA();
    //     // no entity saved in database here, so the record should not exist, and a HTTP 404 should be returned
    //     String drugJson = objectMapper.writeValueAsString(drug);

    //     mockMvc.perform(
    //         MockMvcRequestBuilders.patch("/drugs/1")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isNotFound()
    //     );
    // }

    // test to check that partialUpdateDrug method returns a HTTP 200 Ok code on updating existing record
    // @Test
    // public void testThatPartialUpdateDrugReturnsHttp200Ok() throws Exception{
    //     DrugEntity drugEntity = TestData.createTestDrugA();
    //     drugService.saveDrug(drugEntity.getLicenceNo(), drugEntity);
    //     DrugDto drugDto = TestData.createTestDrugDtoA();
    //     // update product name of Dto to be passed in
    //     drugDto.setProductName("UPDATED");
    //     String drugJson = objectMapper.writeValueAsString(drugDto);
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.patch("/drugs/" + URLEncoder.encode(drugEntity.getLicenceNo(), StandardCharsets.UTF_8))
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isOk()
    //     );
    // }

    // test to check that partialUpdateDrug method returns expected updated drug
    // @Test
    // public void testThatPartialUpdateDrugReturnsUpdatedRecord() throws Exception{
    //     DrugEntity drugEntity = TestData.createTestDrugA();
    //     drugService.saveDrug(drugEntity.getLicenceNo(), drugEntity);
    //     DrugDto drugDto = TestData.createTestDrugDtoA();
    //     // update product name of Dto to be passed in
    //     drugDto.setProductName("UPDATED");
    //     String drugJson = objectMapper.writeValueAsString(drugDto);
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.patch("/drugs/" + URLEncoder.encode(drugEntity.getLicenceNo(), StandardCharsets.UTF_8))
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(drugJson)
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.licenceNo").value(drugDto.getLicenceNo())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.productName").value("UPDATED")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.manufacturer").value(drugDto.getManufacturer())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.strength").value(drugDto.getStrength())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.dosageForm").value(drugDto.getDosageForm())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.activeSubstance").value(drugDto.getActiveSubstance())
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.isAvailable").value(drugDto.getIsAvailable())
    //     );
    // }

//////////////////////////////////////////////////////// FIND METHODS TESTS //////////////////////////////////////////////////////////////////

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
        drugService.saveDrug(drug.getLicenceNo(),drug);
        mockMvc.perform(
        MockMvcRequestBuilders.get("/drugs")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].licenceNo").value("PA0749/067/001")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].productName").value("Amlodipine Teva 5 mg Tablets")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].manufacturer").value("Teva Pharma B.V")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].strength").value("5 mg")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].dosageForm").value("Tablet")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].activeSubstance").value("Amlodipine")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.content[0].isAvailable").value(true)
        );
    }

   // test to check that GetDrug method returns a HTTP 200 Ok code when record is found
    // @Test
    // public void testThatGetDrugReturnsHttp200WhenRecordExists() throws Exception {
    //     DrugEntity drug = TestData.createTestDrugA();
    //     // persist Drug entity in the test database
    //     drugService.saveDrug(drug.getLicenceNo(), drug);
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.get("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
    //             .contentType(MediaType.APPLICATION_JSON)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isOk()
    //     );
    // }

    // test to check that GetDrug method returns a HTTP 404 Not Found code when record doesn't exist
    // @Test
    // public void testThatGetDrugReturnsHttp404WhenNoRecordExists() throws Exception {
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.get("/drugs/nonExistentLicenceNumber")
    //             .contentType(MediaType.APPLICATION_JSON)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isNotFound()
    //     );
    // }

    // test to check that GetDrug method returns correct record when record exists
    // @Test
    // public void testThatGetDrugReturnsCorrectRecordWhenRecordExists() throws Exception {
    //     DrugEntity drug = TestData.createTestDrugA();
    //     // persist Drug entity in the test database
    //     drugService.saveDrug(drug.getLicenceNo(), drug);
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.get("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
    //             .contentType(MediaType.APPLICATION_JSON)
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.licenceNo").value("PA0749/067/001")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.productName").value("Amlodipine Teva 5 mg Tablets")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.manufacturer").value("Teva Pharma B.V")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.strength").value("5 mg")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.dosageForm").value("Tablet")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.activeSubstance").value("Amlodipine")
    //     ).andExpect(
    //         MockMvcResultMatchers.jsonPath("$.isAvailable").value(true)
    //     );
    // }

    // test to check that ListAlternativeDrugNames method returns a HTTP 200 Ok code when record is found
    @Test
    public void testThatListAlternativeDrugNamesReturnsHttp200WhenRecordIsFound() throws Exception {
        DrugEntity drug = TestData.createTestDrugA();
        // persist Drug entity in the test database
        drugService.saveDrug(drug.getLicenceNo(), drug);
        mockMvc.perform(
            MockMvcRequestBuilders.get("/drugs/search-by-product-name/" + drug.getProductName())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    // test to check that ListAllAlternativeDrugMethods method returns a HTTP 404 Not Found code when record doesn't exist
    @Test
    public void testThatListAlternativeDrugNamesReturnsHttp404WhenNoRecordIsFound() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/drugs/search-by-product-name/nonExistentProductName")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    // test to check that ListAlternativeDrugNames method returns correct record when record exists
    @Test
    public void testThatListAlternativeDrugNamesReturnsCorrectRecordsWhenRecordIsFound() throws Exception {
        DrugEntity drugA = TestData.createTestDrugA();
        DrugEntity drugD = TestData.createTestDrugD();
        DrugEntity drugE = TestData.createTestDrugE();

        drugService.saveDrug(drugA.getLicenceNo(), drugA);
        drugService.saveDrug(drugD.getLicenceNo(), drugD);
        drugService.saveDrug(drugE.getLicenceNo(), drugE);

        MvcResult result = mockMvc.perform( // Correct placement of the closing )
                MockMvcRequestBuilders.get("/drugs/search-by-product-name/" + drugA.getProductName())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content[*].productName", hasItems(
                        drugA.getProductName(),
                        drugD.getProductName(),
                        drugE.getProductName()
                ))
        ).andReturn(); 

        String response = result.getResponse().getContentAsString();
        System.out.println("JSON Response: " + response);
    }
//////////////////////////////////////////////////////// DELETE METHOD TESTS /////////////////////////////////////////////////////////

    // test to check that deleteDrug method returns a HTTP 204 No Content code when non-existent record is deleted
    // @Test
    // public void testThatDeleteNonExistentDrugReturnsHttp204() throws Exception {
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.delete("/drugs/nonExistentLicenceNumber")
    //             .contentType(MediaType.APPLICATION_JSON)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isNoContent()
    //     );
    // }

    // // test to check that deleteDrug method returns a HTTP 204 No Content code when existing record is deleted
    // @Test
    // public void testThatDeleteExistingDrugReturnsHttp204() throws Exception {
    //     DrugEntity drug = TestData.createTestDrugA();
    //     // persist Drug entity in the test database
    //     drugService.saveDrug(drug.getLicenceNo(), drug);
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.delete("/drugs/" + URLEncoder.encode(drug.getLicenceNo(), StandardCharsets.UTF_8))
    //             .contentType(MediaType.APPLICATION_JSON)
    //     ).andExpect(
    //         MockMvcResultMatchers.status().isNoContent()
    //     );
    // }
}